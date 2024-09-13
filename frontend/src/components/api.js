import axios from 'axios';

const API_URL = 'http://localhost:8080';

// Axios instance to automatically attach Authorization header if token is present
const api = axios.create({
    baseURL: API_URL,
});

// Interceptor to add the JWT token to the Authorization header on each request
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('tokenValue');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Login API call
export const login = async (email, password) => {
    try {
        const response = await api.post('/login', {email, password});
        const token = response.data.tokenValue; // Assuming the backend sends the token as `tokenValue`

        // Store the token in localStorage to persist session
        localStorage.setItem('tokenValue', token);

        return response.data;
    } catch (error) {
        console.error('Login error:', error); // Log the error for debugging
        if (error.response && error.response.data) {
            throw new Error(error.response.data.message || 'Login failed');
        }
        throw new Error('Login failed');
    }
};

// Register API call
export const register = async (firstName, lastName, email, password) => {
    try {
        const response = await api.post('/register', {firstName, lastName, email, password});
        return response.data;
    } catch (error) {
        console.error('Error response:', error); // Log the entire error object for debugging
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data)); // Pass error details from backend
        } else {
            throw new Error('Registration failed');
        }
    }
};

export const note = async (userId, title, text, date, grade) => {
    try {
        const response = await api.post('/notes', {userId, title, text, date, grade});
        return response.data;
    } catch (error) {
        console.error('Error response:', error); // Log the entire error object for debugging
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data)); // Pass error details from backend
        } else {
            throw new Error('Failed creating a note');
        }
    }
}