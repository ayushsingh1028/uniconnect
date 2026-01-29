// API Configuration and Service Layer
const API_BASE_URL = 'http://localhost:8080/api';

// Token Management
const TOKEN_KEY = 'uniconnect_token';
const USER_KEY = 'uniconnect_user';

const TokenManager = {
    getToken() {
        return localStorage.getItem(TOKEN_KEY);
    },

    setToken(token) {
        localStorage.setItem(TOKEN_KEY, token);
    },

    getUser() {
        const user = localStorage.getItem(USER_KEY);
        return user ? JSON.parse(user) : null;
    },

    setUser(user) {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    },

    clear() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    },

    isAuthenticated() {
        return !!this.getToken();
    }
};

// API Call Wrapper
async function apiCall(endpoint, options = {}) {
    const token = TokenManager.getToken();

    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` }),
            ...options.headers
        },
        ...options
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        // Handle 401 Unauthorized
        if (response.status === 401) {
            TokenManager.clear();
            window.location.href = '/login.html';
            throw new Error('Unauthorized');
        }

        let data;
        const contentType = response.headers.get('content-type');

        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = { error: await response.text() };
        }

        if (!response.ok) {
            throw new Error(data.error || data.message || `Request failed with status ${response.status}`);
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Authentication API
const AuthAPI = {
    async login(email, password) {
        const response = await apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });

        TokenManager.setToken(response.token);
        TokenManager.setUser({
            email: response.email,
            name: response.name,
            role: response.role,
            universityId: response.universityId,
            userId: response.userId
        });

        return response;
    },

    async register(data) {
        const response = await apiCall('/auth/register', {
            method: 'POST',
            body: JSON.stringify(data)
        });

        TokenManager.setToken(response.token);
        TokenManager.setUser({
            email: response.email,
            name: response.name,
            role: response.role,
            universityId: response.universityId,
            userId: response.userId
        });

        return response;
    },

    logout() {
        TokenManager.clear();
        window.location.href = '/index.html';
    },

    async getCurrentUser() {
        return await apiCall('/users/me');
    }
};

// Posts API
const PostsAPI = {
    async getPosts(type = 'NORMAL') {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : null;
        let query = `?universityId=${uniId || ''}`;
        if (type) query += `&type=${type}`;
        return await apiCall(`/posts/feed${query}`);
    },

    async getTopContributors() {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : null;
        return await apiCall(`/posts/top-contributors?universityId=${uniId || ''}`);
    },

    async createPost(content, type = 'NORMAL', isAnonymous = false) {
        return await apiCall('/posts', {
            method: 'POST',
            body: JSON.stringify({ content, type, isAnonymous })
        });
    },

    async deletePost(postId) {
        return await apiCall(`/posts/${postId}`, {
            method: 'DELETE'
        });
    },

    async likePost(postId) {
        return await apiCall(`/posts/${postId}/like`, {
            method: 'POST'
        });
    },

    async addComment(postId, content) {
        return await apiCall(`/posts/${postId}/comment`, {
            method: 'POST',
            body: JSON.stringify({ content })
        });
    },

    async deleteComment(commentId) {
        return await apiCall(`/posts/comments/${commentId}`, {
            method: 'DELETE'
        });
    }
};

// PYQ API
const PYQAPI = {
    async getPYQs(subject = null, year = null) {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : null;
        let query = `?universityId=${uniId || ''}`;
        if (subject) query += `&subject=${subject}`;
        if (year) query += `&year=${year}`;
        return await apiCall(`/pyqs${query}`);
    },

    async uploadPYQ(formData) {
        const token = TokenManager.getToken();
        const response = await fetch(`${API_BASE_URL}/pyqs/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || data.message || 'Upload failed');
        }

        return data;
    },

    async deletePYQ(id) {
        return await apiCall(`/pyqs/${id}`, {
            method: 'DELETE'
        });
    }
};

// Alumni API
const AlumniAPI = {
    async getProfiles() {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : '';
        return await apiCall(`/alumni?universityId=${uniId}`);
    },

    async createProfile(data) {
        return await apiCall('/alumni/profile', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }
};

// Marketplace API
const MarketplaceAPI = {
    async getItems(category = null) {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : '';
        let query = `?universityId=${uniId}`;
        if (category) query += `&category=${category}`;
        return await apiCall(`/marketplace/items${query}`);
    },

    async createListing(data) {
        return await apiCall('/marketplace/items', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    async deleteItem(itemId) {
        return await apiCall(`/marketplace/items/${itemId}`, {
            method: 'DELETE'
        });
    }
};

// Events API
const EventsAPI = {
    async getEvents() {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : '';
        return await apiCall(`/events?universityId=${uniId}`);
    }
};

// Clubs API
const ClubsAPI = {
    async getClubs() {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : '';
        return await apiCall(`/clubs?universityId=${uniId}`);
    }
};

// PG API
const PGAPI = {
    async getPGs() {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : '';
        return await apiCall(`/pg?universityId=${uniId}`);
    }
};

// Food Courts API
const FoodCourtsAPI = {
    async getFoodCourts() {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : '';
        return await apiCall(`/food?universityId=${uniId}`);
    }
};

// Chat API
const ChatAPI = {
    async sendMessage(receiverId, content, itemId = null) {
        return await apiCall('/chat/send', {
            method: 'POST',
            body: JSON.stringify({ receiverId, content, itemId })
        });
    },

    async getConversation(otherUserId) {
        return await apiCall(`/chat/conversation/${otherUserId}`);
    },

    async getChatPartners() {
        return await apiCall('/chat/partners');
    }
};

const SearchAPI = {
    async search(query) {
        const user = TokenManager.getUser();
        const uniId = user ? user.universityId : '';
        return await apiCall(`/search?universityId=${uniId}&query=${encodeURIComponent(query)}`);
    }
};
