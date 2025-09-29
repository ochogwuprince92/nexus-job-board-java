import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  IconButton,
  Menu,
  MenuItem,
  Avatar,
  Chip,
} from '@mui/material';
import {
  AccountCircle,
  Work,
  Dashboard,
  Assignment,
  ExitToApp,
} from '@mui/icons-material';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../store/store';
import { logoutAsync } from '../../store/slices/authSlice';
import { UserRole } from '../../types';

/**
 * Header component following SRP
 * - Single responsibility: Navigation and user menu
 */

const Header: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user, isAuthenticated } = useSelector((state: RootState) => state.auth);
  
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    dispatch(logoutAsync() as any);
    handleClose();
    navigate('/');
  };

  const getRoleColor = (role: UserRole) => {
    switch (role) {
      case UserRole.ADMIN:
        return 'error';
      case UserRole.EMPLOYER:
        return 'primary';
      case UserRole.JOB_SEEKER:
        return 'success';
      default:
        return 'default';
    }
  };

  return (
    <AppBar position="sticky" elevation={1}>
      <Toolbar>
        <Typography
          variant="h6"
          component={Link}
          to="/"
          sx={{
            flexGrow: 1,
            textDecoration: 'none',
            color: 'inherit',
            fontWeight: 'bold',
          }}
        >
          Nexus Jobs
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Button
            color="inherit"
            component={Link}
            to="/jobs"
            startIcon={<Work />}
          >
            Jobs
          </Button>

          {isAuthenticated ? (
            <>
              <Button
                color="inherit"
                component={Link}
                to="/dashboard"
                startIcon={<Dashboard />}
              >
                Dashboard
              </Button>

              {user?.role === UserRole.JOB_SEEKER && (
                <Button
                  color="inherit"
                  component={Link}
                  to="/applications"
                  startIcon={<Assignment />}
                >
                  My Applications
                </Button>
              )}

              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Chip
                  label={user?.role.replace('_', ' ')}
                  color={getRoleColor(user?.role || UserRole.JOB_SEEKER)}
                  size="small"
                />
                <IconButton
                  size="large"
                  aria-label="account of current user"
                  aria-controls="menu-appbar"
                  aria-haspopup="true"
                  onClick={handleMenu}
                  color="inherit"
                >
                  <Avatar sx={{ width: 32, height: 32 }}>
                    {user?.firstName?.charAt(0)}
                  </Avatar>
                </IconButton>
              </Box>

              <Menu
                id="menu-appbar"
                anchorEl={anchorEl}
                anchorOrigin={{
                  vertical: 'bottom',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorEl)}
                onClose={handleClose}
              >
                <MenuItem onClick={() => { navigate('/profile'); handleClose(); }}>
                  <AccountCircle sx={{ mr: 1 }} />
                  Profile
                </MenuItem>
                <MenuItem onClick={() => { navigate('/dashboard'); handleClose(); }}>
                  <Dashboard sx={{ mr: 1 }} />
                  Dashboard
                </MenuItem>
                <MenuItem onClick={handleLogout}>
                  <ExitToApp sx={{ mr: 1 }} />
                  Logout
                </MenuItem>
              </Menu>
            </>
          ) : (
            <>
              <Button color="inherit" component={Link} to="/login">
                Login
              </Button>
              <Button
                variant="outlined"
                color="inherit"
                component={Link}
                to="/register"
              >
                Sign Up
              </Button>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
