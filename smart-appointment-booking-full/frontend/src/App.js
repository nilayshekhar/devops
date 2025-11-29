import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import theme from './styles/theme';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/common/Navbar';
import Footer from './components/common/Footer';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import UserDashboard from './components/user/UserDashboard';
import AdminDashboard from './components/admin/AdminDashboard';
import AppointmentForm from './components/appointments/AppointmentForm';
import PrivateRoute from './components/auth/PrivateRoute';
import './styles/App.css';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <div className="App">
            <Navbar />
            <main className="main-content">
              <Routes>
                {/* Public Routes */}
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />

                {/* Protected Routes */}
                <Route
                  path="/dashboard"
                  element={
                    <PrivateRoute>
                      <UserDashboard />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/admin"
                  element={
                    <PrivateRoute>
                      <AdminDashboard />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/appointments/new"
                  element={
                    <PrivateRoute>
                      <AppointmentForm />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/appointments/edit/:id"
                  element={
                    <PrivateRoute>
                      <AppointmentForm />
                    </PrivateRoute>
                  }
                />

                {/* 404 Not Found */}
                <Route path="*" element={<NotFound />} />
              </Routes>
            </main>
            <Footer />
          </div>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

// Home Component
const Home = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 6 }}>
      <Grid container spacing={4} alignItems="center">
        <Grid item xs={12} md={6}>
          <Typography variant="h1" color="primary" gutterBottom>
            Smart Appointment Booking
          </Typography>
          <Typography variant="h5" color="text.secondary" paragraph>
            Book appointments with service providers easily and efficiently.
          </Typography>
          <Grid container spacing={2} sx={{ mt: 2 }}>
            <Grid item>
              <Button href="/register" variant="contained" color="primary" size="large">
                Get Started
              </Button>
            </Grid>
            <Grid item>
              <Button href="/login" variant="outlined" color="secondary" size="large">
                Login
              </Button>
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={12} md={6}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6">🏥 Medical Consultations</Typography>
                  <Typography variant="body2">Book appointments with doctors and healthcare professionals</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6">✂️ Salon & Barber</Typography>
                  <Typography variant="body2">Schedule your haircut and styling appointments</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6">💼 Business Consulting</Typography>
                  <Typography variant="body2">Connect with business consultants and advisors</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Card>
                <CardContent>
                  <Typography variant="h6">⚖️ Legal Services</Typography>
                  <Typography variant="body2">Book consultations with legal professionals</Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Container>
  );
};

// 404 Not Found Component
const NotFound = () => {
  return (
    <Container maxWidth="sm" sx={{ py: 8, textAlign: 'center' }}>
      <Typography variant="h2" color="error" gutterBottom>
        404 - Page Not Found
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        The page you're looking for doesn't exist.
      </Typography>
      <Button href="/" variant="contained" color="primary">
        Go Home
      </Button>
    </Container>
  );
};

export default App;