import React, { createContext, useContext, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../store/store';
import { getCurrentUserAsync } from '../store/slices/authSlice';

/**
 * AuthContext following SRP
 * - Single responsibility: Provide authentication context
 */

interface AuthContextType {
  // Context methods can be added here if needed
}

const AuthContext = createContext<AuthContextType>({});

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const dispatch = useDispatch();
  const { accessToken, isAuthenticated } = useSelector(
    (state: RootState) => state.auth
  );

  useEffect(() => {
    // If we have a token but no user data, fetch the current user
    if (accessToken && !isAuthenticated) {
      dispatch(getCurrentUserAsync() as any);
    }
  }, [dispatch, accessToken, isAuthenticated]);

  const value: AuthContextType = {
    // Add context methods here if needed
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
