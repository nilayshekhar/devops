import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import appointmentService from '../../services/appointmentService';
import AppointmentCard from '../appointments/AppointmentCard';
import LoadingSpinner from '../common/LoadingSpinner';
import { Container, Box, Typography, Button, Grid, Card, CardContent, Tabs, Tab, Alert } from '@mui/material';

const UserDashboard = () => {
  const { user, isServiceProvider } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [upcomingAppointments, setUpcomingAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('upcoming');
  const [info, setInfo] = useState('');

  const fetchAppointments = React.useCallback(async () => {
    try {
      setLoading(true);
      setError('');

      let allAppointmentsResponse;
      let upcomingResponse;

      if (isServiceProvider) {
        // Fetch appointments for service provider
        allAppointmentsResponse = await appointmentService.getAppointmentsByProvider(user.id);
        upcomingResponse = allAppointmentsResponse; // Filter client-side
      } else {
        // Fetch appointments for customer
        allAppointmentsResponse = await appointmentService.getAppointmentsByCustomer(user.id);
        upcomingResponse = await appointmentService.getUpcomingAppointments(user.id);
      }

      if (allAppointmentsResponse.success) {
        const all = allAppointmentsResponse.data || [];
        setAppointments(all);
        // Check for expired unconfirmed appointments that may have been auto-removed
        const now = new Date();
        const expiredUnconfirmed = all.filter(
          apt => apt.status === 'PENDING' && new Date(apt.appointmentDateTime) < now
        );
        if (expiredUnconfirmed.length > 0) {
          setInfo(`${expiredUnconfirmed.length} expired unconfirmed appointment(s) have been auto-removed.`);
        } else {
          setInfo('');
        }
      }

      if (upcomingResponse.success) {
        setUpcomingAppointments(upcomingResponse.data || []);
      }
    } catch (err) {
      setError('Failed to fetch appointments. Please try again.');
      console.error('Error fetching appointments:', err);
    } finally {
      setLoading(false);
    }
  }, [user, isServiceProvider]);

  useEffect(() => {
    fetchAppointments(); // Fetch appointments when user changes
  }, [fetchAppointments]);

  const handleCancelAppointment = async (id) => {
    if (window.confirm('Are you sure you want to cancel this appointment?')) {
      try {
        await appointmentService.updateAppointmentStatus(id, 'CANCELLED');
        fetchAppointments(); // Refresh the list
      } catch (err) {
        alert('Failed to cancel appointment');
        console.error(err);
      }
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      const result = await appointmentService.updateAppointmentStatus(id, newStatus);
      if (result && result.success) {
        fetchAppointments(); // Refresh the list
      } else {
        // Show backend error message if available
        alert(result && result.message ? result.message : 'Failed to update appointment status');
      }
    } catch (err) {
      // Show backend error message if available
      const backendMsg = err?.response?.data?.message;
      alert(backendMsg ? backendMsg : 'Failed to update appointment status');
      console.error(err);
    }
  };

  const filteredAppointments = activeTab === 'upcoming'
    ? upcomingAppointments
    : appointments.filter(apt => {
        if (activeTab === 'pending') return apt.status === 'PENDING';
        if (activeTab === 'confirmed') return apt.status === 'CONFIRMED';
        if (activeTab === 'completed') return apt.status === 'COMPLETED';
        if (activeTab === 'cancelled') return apt.status === 'CANCELLED';
        return true;
      });

  if (loading) return <LoadingSpinner />;

  const tabOptions = [
    { label: 'Upcoming', value: 'upcoming' },
    { label: 'All', value: 'all' },
    { label: 'Pending', value: 'pending' },
    { label: 'Confirmed', value: 'confirmed' },
    { label: 'Completed', value: 'completed' },
    { label: 'Cancelled', value: 'cancelled' },
  ];

  return (
    <Container maxWidth="lg" sx={{ py: 6 }}>
      {info && <Alert severity="info" sx={{ mb: 2 }}>{info}</Alert>}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h3" color="primary" gutterBottom>
            Welcome, {user?.name}!
          </Typography>
          <Typography variant="subtitle1" color="text.secondary">
            {isServiceProvider
              ? 'Manage your appointments and schedule'
              : 'View and manage your bookings'}
          </Typography>
        </Box>
        {!isServiceProvider && (
          <Button component={Link} to="/appointments/new" variant="contained" color="primary" size="large">
            + Book New Appointment
          </Button>
        )}
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* Stats Cards */}
      <Grid container spacing={2} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6">Total Appointments</Typography>
              <Typography variant="h4" color="primary">{appointments.length}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6">Upcoming</Typography>
              <Typography variant="h4" color="primary">{upcomingAppointments.length}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6">Pending</Typography>
              <Typography variant="h4" color="primary">{appointments.filter(a => a.status === 'PENDING').length}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography variant="h6">Completed</Typography>
              <Typography variant="h4" color="primary">{appointments.filter(a => a.status === 'COMPLETED').length}</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tabs */}
      <Tabs
        value={activeTab}
        onChange={(_, value) => setActiveTab(value)}
        variant="scrollable"
        scrollButtons="auto"
        sx={{ mb: 3 }}
      >
        {tabOptions.map(tab => (
          <Tab key={tab.value} label={tab.label} value={tab.value} />
        ))}
      </Tabs>

      {/* Appointments List */}
      <Box sx={{ mt: 2 }}>
        <Typography variant="h5" gutterBottom>
          {activeTab.charAt(0).toUpperCase() + activeTab.slice(1)} Appointments
        </Typography>

        {filteredAppointments.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography variant="body1" color="text.secondary">No appointments found.</Typography>
            {!isServiceProvider && activeTab === 'upcoming' && (
              <Button component={Link} to="/appointments/new" variant="contained" color="primary" sx={{ mt: 2 }}>
                Book Your First Appointment
              </Button>
            )}
          </Box>
        ) : (
          <Grid container spacing={2}>
            {filteredAppointments.map((appointment) => (
              <Grid item xs={12} sm={6} md={4} key={appointment.id}>
                <AppointmentCard
                  appointment={appointment}
                  isServiceProvider={isServiceProvider}
                  onCancel={handleCancelAppointment}
                  onStatusChange={handleStatusChange}
                />
              </Grid>
            ))}
          </Grid>
        )}
      </Box>
    </Container>
  );
};

export default UserDashboard;