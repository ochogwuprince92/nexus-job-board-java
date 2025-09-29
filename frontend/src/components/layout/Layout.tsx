import React from 'react';
import { Box, Container } from '@mui/material';
import Header from './Header';
import Footer from './Footer';

/**
 * Layout component following SRP
 * - Single responsibility: Provide consistent page layout
 */

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        minHeight: '100vh',
      }}
    >
      <Header />
      <Container
        component="main"
        maxWidth="xl"
        sx={{
          flexGrow: 1,
          py: 3,
        }}
      >
        {children}
      </Container>
      <Footer />
    </Box>
  );
};

export default Layout;
