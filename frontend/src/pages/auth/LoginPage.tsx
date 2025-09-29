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
  Divider,
} from '@mui/material';
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import { RootState } from '../../store/store';
import { loginAsync, clearError } from '../../store/slices/authSlice';

/**
 * LoginPage component following SRP
 * - Single responsibility: Handle user login
 */

const validationSchema = Yup.object({
  username: Yup.string()
    .required('Email or phone number is required'),
  password: Yup.string()
    .min(8, 'Password must be at least 8 characters')
    .required('Password is required'),
});

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  
  const { isLoading, error, isAuthenticated } = useSelector(
    (state: RootState) => state.auth
  );

  const from = (location.state as any)?.from?.pathname || '/dashboard';

  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, from]);

  useEffect(() => {
    // Clear any existing errors when component mounts
    dispatch(clearError());
  }, [dispatch]);

  const handleSubmit = async (values: { username: string; password: string }) => {
    dispatch(loginAsync(values) as any);
  };

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
        <CardContent sx={{ p: 4 }}>
          <Typography
            variant="h4"
            component="h1"
            gutterBottom
            align="center"
            sx={{ fontWeight: 'bold', mb: 3 }}
          >
            Welcome Back
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <Formik
            initialValues={{ username: '', password: '' }}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}
          >
            {({ errors, touched, isSubmitting }) => (
              <Form>
                <Field name="username">
                  {({ field }: any) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Email or Phone Number"
                      margin="normal"
                      error={touched.username && !!errors.username}
                      helperText={touched.username && errors.username}
                      disabled={isLoading}
                    />
                  )}
                </Field>

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

                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={isLoading || isSubmitting}
                  sx={{ mt: 3, mb: 2, py: 1.5 }}
                >
                  {isLoading ? (
                    <CircularProgress size={24} color="inherit" />
                  ) : (
                    'Sign In'
                  )}
                </Button>
              </Form>
            )}
          </Formik>

          <Box sx={{ textAlign: 'center', mt: 2 }}>
            <Link
              component={RouterLink}
              to="/forgot-password"
              variant="body2"
              underline="hover"
            >
              Forgot your password?
            </Link>
          </Box>

          <Divider sx={{ my: 3 }}>
            <Typography variant="body2" color="text.secondary">
              OR
            </Typography>
          </Divider>

          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Don't have an account?{' '}
              <Link
                component={RouterLink}
                to="/register"
                variant="body2"
                underline="hover"
                sx={{ fontWeight: 'medium' }}
              >
                Sign up here
              </Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default LoginPage;
