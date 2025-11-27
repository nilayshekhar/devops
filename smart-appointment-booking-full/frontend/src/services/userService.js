import api from './api';

const userService = {
  // Get all users
  getAllUsers: async () => {
  const response = await api.get('/v1/users');
    return response.data;
  },

  // Get user by ID
  getUserById: async (id) => {
  const response = await api.get(`/v1/users/${id}`);
    return response.data;
  },

  // Get all service providers
  getAllServiceProviders: async () => {
  const response = await api.get('/v1/users/providers');
    return response.data;
  },

  // Create user
  createUser: async (userData) => {
  const response = await api.post('/v1/users', userData);
    return response.data;
  },

  // Update user
  updateUser: async (id, userData) => {
  const response = await api.put(`/v1/users/${id}`, userData);
    return response.data;
  },

  // Delete user
  deleteUser: async (id) => {
  const response = await api.delete(`/v1/users/${id}`);
    return response.data;
  },

  // Search users
  searchUsers: async (name) => {
  const response = await api.get(`/v1/users/search?name=${name}`);
    return response.data;
  },
};

export default userService;