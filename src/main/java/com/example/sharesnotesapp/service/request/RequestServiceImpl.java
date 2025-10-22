package com.example.sharesnotesapp.service.request;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.Status;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.RequestRequestDto;
import com.example.sharesnotesapp.repository.RequestRepository;
import com.example.sharesnotesapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public void checkRequests(List<Request> requests) {
        if (!requests.isEmpty()) {
            for (Request request : requests) {
                if (request.getStatus().equals(Status.PENDING)) {
                    throw new IllegalArgumentException("There is already a request created");
                } else if (request.getStatus().equals(Status.ACCEPTED)) {
                    throw new IllegalArgumentException("Cannot send another request");
                }
            }
        }
    }

    @Override
    public Request sendRequest(RequestRequestDto requestDto) {
        User sender = userRepository.findById(requestDto.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
        User receiver = userRepository.findUserByEmail(requestDto.getReceiverEmail())
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send a request to yourself");
        }

        List<Request> existingRequests = requestRepository.getRequestsBySenderAndReceiver(sender, receiver);
        List<Request> requestsFromReceiver = requestRepository.getRequestsBySenderAndReceiver(receiver, sender);

        checkRequests(existingRequests);
        checkRequests(requestsFromReceiver);

        Request request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .sentAt(LocalDateTime.now())
                .build();

        return requestRepository.save(request);

    }

    @Override
    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if (!request.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Cannot delete a non-pending request");
        }

        requestRepository.deleteById(id);
    }

    @Override
    public void acceptRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if (!request.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Cannot accept a non-pending request");
        }

        request.setStatus(Status.ACCEPTED);
        requestRepository.save(request);
        addToFriendList(request.getSender(), request.getReceiver());
    }

    @Override
    public void declineRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Request does not exist"));
        if (!request.getStatus().equals(Status.PENDING)) {
            throw new IllegalArgumentException("Cannot decline a non-pending request");
        }

        request.setStatus(Status.DECLINED);
        requestRepository.save(request);
    }

    @Override
    public List<Request> getSentRequests(User user) {
        return requestRepository.getRequestsBySenderAndStatusOrderBySentAtDesc(user, Status.PENDING);
    }

    @Override
    public List<Request> getReceivedRequests(User user) {
        return requestRepository.getRequestsByReceiverAndStatusOrderBySentAtDesc(user, Status.PENDING);
    }

    @Override
    public Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request with id %s does not exist", id)));
    }

    @Override
    public void addToFriendList(User user, User friend) {
        if (user.getFriendList().contains(friend) || friend.getFriendList().contains(user)) {
            throw new IllegalArgumentException("Users are already friends");
        }

        if (user.getId().equals(friend.getId())) {
            throw new IllegalArgumentException("Cannot add yourself to friend list");
        }

        user.getFriendList().add(friend);
        friend.getFriendList().add(user);
        userRepository.save(user);
        userRepository.save(friend);
    }

    @Transactional
    @Override
    public void removeFromFriendList(User user, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s does not exist", friendId)));

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Hibernate.initialize(managedUser.getFriendList());

        if (user.getId().equals(friendId)) {
            throw new IllegalArgumentException("Must provide different users");
        }

        if (!managedUser.getFriendList().contains(friend) && !friend.getFriendList().contains(managedUser)) {
            throw new EntityNotFoundException("Users must be friends to remove from friend list");
        }

        Hibernate.initialize(friend.getFriendList());

        managedUser.getFriendList().remove(friend);
        friend.getFriendList().remove(managedUser);

        userRepository.save(managedUser);
        userRepository.save(friend);

        List<Request> senderRequests = requestRepository.getRequestsBySenderAndReceiver(managedUser, friend);
        List<Request> receiverRequests = requestRepository.getRequestsBySenderAndReceiver(friend, managedUser);
        if (!senderRequests.isEmpty()) {
            requestRepository.deleteAll(senderRequests);
        }
        if (!receiverRequests.isEmpty()) {
            requestRepository.deleteAll(receiverRequests);
        }
    }
}
