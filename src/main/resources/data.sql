-- jane is friend with bob, mary and samantha; jane is used in testing
-- INSERT INTO user_friends (user_id, friend_id) VALUES
--                                                   (1, 2), (2, 1),
--                                                   (1, 3), (3, 1),
--                                                   (1, 4), (4, 1);

--jane's tags
INSERT INTO tags (id, name, user_id) VALUES
    (1, 'Work', 1), (2, 'Motivation', 1), (3, 'To Do', 1), (4, 'Reminder', 1), (5, 'Other', 1);

--jane's notes
INSERT INTO notes (id, user_id, date, title, text, grade) VALUES
    (1, 1, '2025-05-05', 'My motivation for today', 'Be kind to yourself', 9),
    (2, 1, '2025-05-06', 'Work presentation', 'Had my work presentation and the application crashed', 3),
    (3, 1, '2025-05-08', 'Appointment', 'Need to go to that appointment', 10),
    (4, 1, '2025-05-09', 'Feelings', 'Today was average', 7),
    (5, 1, '2025-05-10', 'Self-reflection', 'Listened to a podcast and it was interesting', 8),
    (6, 1, '2025-04-07', 'Birthday', 'Went out with my friends', 9),
    (7, 1, '2025-05-17', 'Power cut', 'No power for all day', 5),
    (8, 1, '2025-02-05', 'February', 'Want to go somewhere else', 4),
    (9, 1, '2024-12-31', 'Last day', 'I am grateful for this year', 9),
    (10, 1, '2025-05-01', 'Labour day', 'Really need a bbq with my friends', 8),
    (11, 3, '2025-03-04', 'Going out', 'Went out and met with our old friend Jenny', 9),
    (12, 3, '2025-01-13', 'Worst day ever', 'I broke my car and then I lost my laptop', 2);

--note's tags
INSERT INTO note_tags (note_id, tag_id) VALUES
                                            (1, 2),
                                            (1, 4),
                                            (2, 1),
                                            (3, 3),
                                            (5, 5),
                                            (7, 5)
                                            (10, 2);

--jane has request from john
INSERT INTO requests (id, sender_id, receiver_id, status, sent_at) VALUES
    (1, 5, 1, 'PENDING', '2025-05-14');

-- jane shared notes with bob and mary; she received notes from mary
INSERT INTO shares (id, sender_id, receiver_id, note_id, sent_at) VALUES
    (1, 1, 2, 1, '2025-05-05'),
    (1, 1, 3, 1, '2025-05-05'),
    (1, 1, 2, 3, '2025-05-08'),
    (1, 3, 1, 11, '2025-03-06'),
    (1, 3, 1, 12, '2025-01-14');



