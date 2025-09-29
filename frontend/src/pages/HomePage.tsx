import React from 'react';
import {
  Box,
  Typography,
  Button,
  Container,
  Grid,
  Card,
  CardContent,
  Chip,
  TextField,
  InputAdornment,
} from '@mui/material';
import { Search, Work, Business, TrendingUp } from '@mui/icons-material';
import { Link } from 'react-router-dom';

/**
 * HomePage component following SRP
 * - Single responsibility: Display landing page content
 */

const HomePage: React.FC = () => {
  const featuredStats = [
    { label: 'Active Jobs', value: '1,200+', icon: <Work /> },
    { label: 'Companies', value: '300+', icon: <Business /> },
    { label: 'Success Stories', value: '5,000+', icon: <TrendingUp /> },
  ];

  const popularCategories = [
    'Software Development',
    'Data Science',
    'Product Management',
    'Design',
    'Marketing',
    'Sales',
  ];

  return (
    <Box>
      {/* Hero Section */}
      <Box
        sx={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white',
          py: 8,
          mb: 6,
          borderRadius: 2,
        }}
      >
        <Container maxWidth="md">
          <Typography
            variant="h2"
            component="h1"
            gutterBottom
            align="center"
            sx={{ fontWeight: 'bold', mb: 2 }}
          >
            Find Your Dream Job
          </Typography>
          <Typography
            variant="h5"
            align="center"
            sx={{ mb: 4, opacity: 0.9 }}
          >
            Connect with top companies and discover opportunities that match your skills
          </Typography>
          
          {/* Search Bar */}
          <Box sx={{ maxWidth: 600, mx: 'auto', mb: 4 }}>
            <TextField
              fullWidth
              placeholder="Search jobs, companies, or skills..."
              variant="outlined"
              sx={{
                bgcolor: 'white',
                borderRadius: 1,
                '& .MuiOutlinedInput-root': {
                  '& fieldset': { border: 'none' },
                },
              }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
                endAdornment: (
                  <Button
                    variant="contained"
                    sx={{ mr: -1 }}
                    component={Link}
                    to="/jobs"
                  >
                    Search
                  </Button>
                ),
              }}
            />
          </Box>

          <Box sx={{ textAlign: 'center' }}>
            <Button
              variant="contained"
              size="large"
              component={Link}
              to="/jobs"
              sx={{
                bgcolor: 'white',
                color: 'primary.main',
                mr: 2,
                '&:hover': { bgcolor: 'grey.100' },
              }}
            >
              Browse Jobs
            </Button>
            <Button
              variant="outlined"
              size="large"
              component={Link}
              to="/register"
              sx={{
                borderColor: 'white',
                color: 'white',
                '&:hover': { borderColor: 'white', bgcolor: 'rgba(255,255,255,0.1)' },
              }}
            >
              Get Started
            </Button>
          </Box>
        </Container>
      </Box>

      <Container maxWidth="xl">
        {/* Stats Section */}
        <Grid container spacing={4} sx={{ mb: 6 }}>
          {featuredStats.map((stat, index) => (
            <Grid item xs={12} md={4} key={index}>
              <Card sx={{ textAlign: 'center', py: 3 }}>
                <CardContent>
                  <Box sx={{ color: 'primary.main', mb: 2 }}>
                    {React.cloneElement(stat.icon, { sx: { fontSize: 48 } })}
                  </Box>
                  <Typography variant="h4" component="div" gutterBottom>
                    {stat.value}
                  </Typography>
                  <Typography variant="body1" color="text.secondary">
                    {stat.label}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {/* Popular Categories */}
        <Box sx={{ mb: 6 }}>
          <Typography variant="h4" component="h2" gutterBottom align="center">
            Popular Categories
          </Typography>
          <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 4 }}>
            Explore opportunities in trending fields
          </Typography>
          
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, justifyContent: 'center' }}>
            {popularCategories.map((category, index) => (
              <Chip
                key={index}
                label={category}
                variant="outlined"
                size="large"
                clickable
                component={Link}
                to={`/jobs?category=${encodeURIComponent(category)}`}
                sx={{ fontSize: '1rem', py: 2 }}
              />
            ))}
          </Box>
        </Box>

        {/* Features Section */}
        <Grid container spacing={4} sx={{ mb: 6 }}>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', p: 3 }}>
              <CardContent>
                <Typography variant="h5" component="h3" gutterBottom>
                  🤖 AI-Powered Matching
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Our advanced AI analyzes your resume and matches you with the most relevant job opportunities.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', p: 3 }}>
              <CardContent>
                <Typography variant="h5" component="h3" gutterBottom>
                  🚀 Real-time Notifications
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Get instant updates on your applications and never miss an opportunity.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%', p: 3 }}>
              <CardContent>
                <Typography variant="h5" component="h3" gutterBottom>
                  💼 Top Companies
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Connect with leading companies across various industries and find your perfect fit.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* CTA Section */}
        <Box
          sx={{
            textAlign: 'center',
            py: 6,
            bgcolor: 'grey.50',
            borderRadius: 2,
            mb: 4,
          }}
        >
          <Typography variant="h4" component="h2" gutterBottom>
            Ready to Start Your Journey?
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
            Join thousands of professionals who found their dream jobs through Nexus
          </Typography>
          <Button
            variant="contained"
            size="large"
            component={Link}
            to="/register"
            sx={{ mr: 2 }}
          >
            Sign Up Now
          </Button>
          <Button
            variant="outlined"
            size="large"
            component={Link}
            to="/jobs"
          >
            Browse Jobs
          </Button>
        </Box>
      </Container>
    </Box>
  );
};

export default HomePage;
