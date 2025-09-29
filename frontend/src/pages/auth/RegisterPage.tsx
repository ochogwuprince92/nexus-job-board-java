import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Link,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Divider,
  Grid,
} from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import { RootState } from '../../store/store';
import { registerAsync, clearError } from '../../store/slices/authSlice';
import { UserRole } from '../../types';

/**
 * RegisterPage component following SRP
 * - Single responsibility: Handle user registration
 */

const validationSchema = Yup.object({
  firstName: Yup.string()
    .min(2, 'First name must be at least 2 characters')
    .required('First name is required'),
  lastName: Yup.string()
    .min(2, 'Last name must be at least 2 characters')
    .required('Last name is required'),
  email: Yup.string()
    .email('Invalid email format')
    .when('phoneNumber', {
      is: (phoneNumber: string) => !phoneNumber,
      then: (schema) => schema.required('Email is required when phone number is not provided'),
      otherwise: (schema) => schema.optional(),
    }),
  phoneNumber: Yup.string()
    .matches(/^\+?[1-9]\d{1,14}$/, 'Invalid phone number format')
    .when('email', {
      is: (email: string) => !email,
      then: (schema) => schema.required('Phone number is required when email is not provided'),
      otherwise: (schema) => schema.optional(),
    }),
  password: Yup.string()
    .min(8, 'Password must be at least 8 characters')
    .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, 'Password must contain at least one lowercase letter, one uppercase letter, and one digit')
    .required('Password is required'),
  confirmPassword: Yup.string()
    .oneOf([Yup.ref('password')], 'Passwords must match')
    .required('Please confirm your password'),
  role: Yup.string()
    .oneOf(Object.values(UserRole), 'Invalid role')
    .required('Please select your role'),
});

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  
  const { isLoading, error, isAuthenticated } = useSelector(
    (state: RootState) => state.auth
  );

  const [registrationSuccess, setRegistrationSuccess] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  useEffect(() => {
    dispatch(clearError());
  }, [dispatch]);

  const handleSubmit = async (values: any) => {
    const { confirmPassword, ...registrationData } = values;
    
    try {
      await dispatch(registerAsync(registrationData) as any).unwrap();
      setRegistrationSuccess(true);
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (error) {
      // Error is handled by the slice
    }
  };

  if (registrationSuccess) {
    return (
      <Box
        sx={{
          minHeight: '80vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          py: 4,
        }}
      >
        <Card sx={{ maxWidth: 400, width: '100%', mx: 2 }}>
          <CardContent sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h5" gutterBottom color="success.main">
              Registration Successful! 🎉
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Your account has been created successfully. Redirecting to login...
            </Typography>
          </CardContent>
        </Card>
      </Box>
    );
  }

  return (
    <Box
      sx={{
        minHeight: '80vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        py: 4,
      }}
    >
      <Card sx={{ maxWidth: 500, width: '100%', mx: 2 }}>
        <CardContent sx={{ p: 4 }}>
          <Typography
            variant="h4"
            component="h1"
            gutterBottom
            align="center"
            sx={{ fontWeight: 'bold', mb: 3 }}
          >
            Join Nexus Jobs
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <Formik
            initialValues={{
              firstName: '',
              lastName: '',
              email: '',
              phoneNumber: '',
              password: '',
              confirmPassword: '',
              role: '',
            }}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}
          >
            {({ errors, touched, values, setFieldValue }) => (
              <Form>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6}>
                    <Field name="firstName">
                      {({ field }: any) => (
                        <TextField
                          {...field}
                          fullWidth
                          label="First Name"
                          error={touched.firstName && !!errors.firstName}
                          helperText={touched.firstName && errors.firstName}
                          disabled={isLoading}
                        />
                      )}
                    </Field>
                  </Grid>
                  
                  <Grid item xs={12} sm={6}>
                    <Field name="lastName">
                      {({ field }: any) => (
                        <TextField
                          {...field}
                          fullWidth
                          label="Last Name"
                          error={touched.lastName && !!errors.lastName}
                          helperText={touched.lastName && errors.lastName}
                          disabled={isLoading}
                        />
                      )}
                    </Field>
                  </Grid>
                </Grid>

                <Field name="email">
                  {({ field }: any) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Email Address"
                      type="email"
                      margin="normal"
                      error={touched.email && !!errors.email}
                      helperText={touched.email && errors.email}
                      disabled={isLoading}
                    />
                  )}
                </Field>

                <Field name="phoneNumber">
                  {({ field }: any) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Phone Number (Optional if email provided)"
                      margin="normal"
                      error={touched.phoneNumber && !!errors.phoneNumber}
                      helperText={touched.phoneNumber && errors.phoneNumber}
                      disabled={isLoading}
                    />
                  )}
                </Field>

                <FormControl fullWidth margin="normal" error={touched.role && !!errors.role}>
                  <InputLabel>I am a...</InputLabel>
                  <Select
                    value={values.role}
                    label="I am a..."
                    onChange={(e) => setFieldValue('role', e.target.value)}
                    disabled={isLoading}
                  >
                    <MenuItem value={UserRole.JOB_SEEKER}>Job Seeker</MenuItem>
                    <MenuItem value={UserRole.EMPLOYER}>Employer</MenuItem>
                  </Select>
                  {touched.role && errors.role && (
                    <Typography variant="caption" color="error" sx={{ ml: 2, mt: 0.5 }}>
                      {errors.role}
                    </Typography>
                  )}
                </FormControl>

                <Field name="password">
                  {({ field }: any) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Password"
                      type="password"
                      margin="normal"
                      error={touched.password && !!errors.password}
                      helperText={touched.password && errors.password}
                      disabled={isLoading}
                    />
                  )}
                </Field>

                <Field name="confirmPassword">
                  {({ field }: any) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Confirm Password"
                      type="password"
                      margin="normal"
                      error={touched.confirmPassword && !!errors.confirmPassword}
                      helperText={touched.confirmPassword && errors.confirmPassword}
                      disabled={isLoading}
                    />
                  )}
                </Field>

                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={isLoading}
                  sx={{ mt: 3, mb: 2, py: 1.5 }}
                >
                  {isLoading ? (
                    <CircularProgress size={24} color="inherit" />
                  ) : (
                    'Create Account'
                  )}
                </Button>
              </Form>
            )}
          </Formik>

          <Divider sx={{ my: 3 }}>
            <Typography variant="body2" color="text.secondary">
              OR
            </Typography>
          </Divider>

          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Already have an account?{' '}
              <Link
                component={RouterLink}
                to="/login"
                variant="body2"
                underline="hover"
                sx={{ fontWeight: 'medium' }}
              >
                Sign in here
              </Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default RegisterPage;
