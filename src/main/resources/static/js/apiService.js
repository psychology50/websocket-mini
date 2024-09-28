import { CONFIG } from './config.js';
import { TokenManager } from "./tokenManager.js";

// API Request Wrapper
export async function apiRequest(url, options = {}) {
    const accessToken = TokenManager.getAccessToken();
    if (accessToken) {
        options.headers = {
            ...options.headers,
            'Authorization': accessToken
        };
    }

    try {
        const response = await fetch(url, options);
        if (response.status === 401) {
            throw new Error('Authentication failed');
        }
        return response;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Login Function
export async function login() {
    const userId = document.getElementById('userId').value;

    try {
        const response = await fetch(`${CONFIG.API_URL}/api/auth/login/${userId}`, {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            TokenManager.setAccessToken(response.headers.get('authorization'));
            return true
        } else {
            return false
        }
    } catch (error) {
        console.error('Login error:', error);
        return false
    }
}

// Logout Function
export async function logout() {
    try {
        await fetch(`${CONFIG.API_URL}/api/auth/logout`, {
            method: 'GET',
            headers: {
                'Authorization': TokenManager.getAccessToken()
            },
            credentials: 'include'
        });
        TokenManager.clearTokens();
        return true;
    } catch (error) {
        console.error('Logout error:', error);
        return false;
    }
}

export const getSocketServerUrl = async () => {
    try {
        const response = await apiRequest(`${CONFIG.API_URL}/ws`, {method: 'GET'});
        const data = await response.json();
        return data["url"];
    } catch (error) {
        console.error("Error fetching server URL:", error);
        throw error;
    }
}