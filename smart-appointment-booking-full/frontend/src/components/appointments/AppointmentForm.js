import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import appointmentService from '../../services/appointmentService';
import userService from '../../services/userService';
import { Container, Box, Typography, TextField, Button, Alert, Card, CardContent, MenuItem, CircularProgress } from '@mui/material';

const AppointmentForm = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const isEditMode = !!id;

  const [formData, setFormData] = useState({
    serviceProviderId: '',
    serviceType: 'DOCTOR',
    appointmentDateTime: '',
    notes: '',
  });

  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProviders();
    if (isEditMode) {
      fetchAppointment();
    }
  }, [id, fetchAppointment, isEditMode]);

  const fetchProviders = async () => {
    try {
      const response = await userService.getAllServiceProviders();
      if (response.success) {
        setProviders(response.data || []);
      }
    } catch (err) {
      console.error('Error fetching providers:', err);
    }
  };

  const fetchAppointment = async () => {
    try {
      const response = await appointmentService.getAppointmentById(id);
      if (response.success) {
        const apt = response.data;
        setFormData({
          serviceProviderId: apt.serviceProviderId,
          serviceType: apt.serviceType,
          appointmentDateTime: apt.appointmentDateTime,
          notes: apt.notes || '',
        });
      }
    } catch (err) {
      setError('Failed to fetch appointment details');
      console.error(err);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const appointmentData = {
        customerId: user.id,
        ...formData,
        serviceProviderId: parseInt(formData.serviceProviderId),
      };

      // Ensure appointmentDateTime includes seconds (yyyy-MM-ddTHH:mm:ss)
      if (
        appointmentData.appointmentDateTime &&
        appointmentData.appointmentDateTime.length === 16
      ) {
        appointmentData.appointmentDateTime += ':00';
      }

      let response;
      if (isEditMode) {
        response = await appointmentService.updateAppointment(id, appointmentData);
      } else {
        response = await appointmentService.createAppointment(appointmentData);
      }

      if (response.success) {
        navigate('/dashboard');
      } else {
        setError(response.message || 'Failed to save appointment');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'An error occurred');
      console.error('Error saving appointment:', err);
    } finally {
      setLoading(false);
    }
  };

  const serviceTypes = [
    { value: 'DOCTOR', label: 'Medical Consultation' },
    { value: 'DENTIST', label: 'Dental Check-up' },
    { value: 'BARBER', label: 'Haircut & Styling' },
    { value: 'SALON', label: 'Beauty Services' },
    { value: 'CONSULTANT', label: 'Business Consultation' },
    { value: 'THERAPIST', label: 'Therapy Session' },
    { value: 'LAWYER', label: 'Legal Consultation' },
    { value: 'MECHANIC', label: 'Vehicle Service' },
    { value: 'OTHER', label: 'Other Services' },
  ];

  // Get minimum datetime (tomorrow at 9 AM)
  const getMinDateTime = () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(9, 0, 0, 0);
    return tomorrow.toISOString().slice(0, 16);
  };

  return (
    <Container maxWidth="sm" sx={{ py: 8 }}>
      <Card sx={{ borderRadius: 4, boxShadow: 4 }}>
        <CardContent>
          <Typography variant="h4" color="primary" gutterBottom align="center">
            {isEditMode ? 'Edit Appointment' : 'Book New Appointment'}
          </Typography>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>
          )}
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
            <TextField
              select
              label="Service Provider *"
              name="serviceProviderId"
              value={formData.serviceProviderId}
              onChange={handleChange}
              fullWidth
              required
              margin="normal"
            >
              <MenuItem value="">Select a provider</MenuItem>
              {providers.map((provider) => (
                <MenuItem key={provider.id} value={provider.id}>
                  {provider.name} - {provider.email}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Service Type *"
              name="serviceType"
              value={formData.serviceType}
              onChange={handleChange}
              fullWidth
              required
              margin="normal"
            >
              {serviceTypes.map((type) => (
                <MenuItem key={type.value} value={type.value}>
                  {type.label}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              label="Date & Time *"
              name="appointmentDateTime"
              type="datetime-local"
              value={formData.appointmentDateTime}
              onChange={handleChange}
              fullWidth
              required
              margin="normal"
              inputProps={{ min: getMinDateTime() }}
              helperText="Appointment must be at least 24 hours in advance"
            />
            <TextField
              label="Notes (Optional)"
              name="notes"
              value={formData.notes}
              onChange={handleChange}
              fullWidth
              multiline
              rows={4}
              margin="normal"
              placeholder="Add any additional information or special requirements"
            />
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
              <Button
                type="button"
                variant="outlined"
                color="secondary"
                onClick={() => navigate('/dashboard')}
                sx={{ mr: 2 }}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                disabled={loading}
                startIcon={loading ? <CircularProgress size={20} /> : null}
              >
                {loading ? 'Saving...' : isEditMode ? 'Update Appointment' : 'Book Appointment'}
              </Button>
            </Box>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default AppointmentForm;