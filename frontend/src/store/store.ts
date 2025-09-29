import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import jobReducer from './slices/jobSlice';
import applicationReducer from './slices/applicationSlice';
import uiReducer from './slices/uiSlice';

/**
 * Redux store configuration following SOLID principles
 * - SRP: Each slice handles single domain
 * - OCP: Easy to add new slices
 * - DIP: Depends on slice abstractions
 */

export const store = configureStore({
  reducer: {
    auth: authReducer,
    jobs: jobReducer,
    applications: applicationReducer,
    ui: uiReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST', 'persist/REHYDRATE'],
      },
    }),
  devTools: process.env.NODE_ENV !== 'production',
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
