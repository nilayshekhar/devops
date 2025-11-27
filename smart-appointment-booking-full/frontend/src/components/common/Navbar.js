import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';

const Navbar = () => {
  const { user, logout, isAuthenticated, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <AppBar position="static" color="inherit" elevation={1} sx={{ mb: 2 }}>
      <Toolbar sx={{ display: 'flex', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography
            component={Link}
            to="/"
            variant="h6"
            color="primary"
            sx={{ textDecoration: 'none', fontWeight: 700 }}
          >
            Smart Appointment
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          {isAuthenticated ? (
            <>
              <Button component={Link} to="/dashboard" color="primary">Dashboard</Button>
              {isAdmin && (
                <Button component={Link} to="/admin" color="primary">Admin</Button>
              )}
              <Typography sx={{ mx: 2 }}>Hello, {user?.name}</Typography>
              <Button onClick={handleLogout} variant="outlined" color="secondary" data-testid="logout-btn">Logout</Button>
            </>
          ) : (
            <>
              <Button component={Link} to="/login" variant="outlined" color="primary">Login</Button>
              <Button component={Link} to="/register" variant="contained" color="secondary">Sign Up</Button>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;