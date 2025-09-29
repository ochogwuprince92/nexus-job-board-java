import React from 'react';
import {
  Box,
  Container,
  Typography,
  Link,
  Grid,
  Divider,
} from '@mui/material';
import { GitHub, LinkedIn, Email } from '@mui/icons-material';

/**
 * Footer component following SRP
 * - Single responsibility: Display footer information
 */

const Footer: React.FC = () => {
  return (
    <Box
      component="footer"
      sx={{
        bgcolor: 'background.paper',
        borderTop: 1,
        borderColor: 'divider',
        py: 4,
        mt: 'auto',
      }}
    >
      <Container maxWidth="xl">
        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <Typography variant="h6" gutterBottom>
              Nexus Jobs
            </Typography>
            <Typography variant="body2" color="text.secondary">
              A modern job board platform built with SOLID principles.
              Connecting talented professionals with amazing opportunities.
            </Typography>
          </Grid>
          
          <Grid item xs={12} md={4}>
            <Typography variant="h6" gutterBottom>
              Quick Links
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Link href="/jobs" color="inherit" underline="hover">
                Browse Jobs
              </Link>
              <Link href="/companies" color="inherit" underline="hover">
                Companies
              </Link>
              <Link href="/about" color="inherit" underline="hover">
                About Us
              </Link>
              <Link href="/contact" color="inherit" underline="hover">
                Contact
              </Link>
            </Box>
          </Grid>
          
          <Grid item xs={12} md={4}>
            <Typography variant="h6" gutterBottom>
              Connect
            </Typography>
            <Box sx={{ display: 'flex', gap: 2 }}>
              <Link
                href="https://github.com/ochogwuprince92"
                color="inherit"
                target="_blank"
                rel="noopener noreferrer"
              >
                <GitHub />
              </Link>
              <Link
                href="https://linkedin.com/in/ochogwuprince"
                color="inherit"
                target="_blank"
                rel="noopener noreferrer"
              >
                <LinkedIn />
              </Link>
              <Link
                href="mailto:ochogwuprince92@gmail.com"
                color="inherit"
              >
                <Email />
              </Link>
            </Box>
          </Grid>
        </Grid>
        
        <Divider sx={{ my: 3 }} />
        
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: 2,
          }}
        >
          <Typography variant="body2" color="text.secondary">
            © 2024 Nexus Jobs. Built with SOLID principles.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Made with ❤️ by Prince Ochogwu
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default Footer;
