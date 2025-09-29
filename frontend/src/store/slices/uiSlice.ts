import { createSlice, PayloadAction } from '@reduxjs/toolkit';

/**
 * UI slice following SRP
 * - Single responsibility: Manage UI state (notifications, loading, etc.)
 */

interface UIState {
  notifications: Notification[];
  isGlobalLoading: boolean;
  sidebarOpen: boolean;
  theme: 'light' | 'dark';
}

interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  autoHide?: boolean;
  duration?: number;
}

const initialState: UIState = {
  notifications: [],
  isGlobalLoading: false,
  sidebarOpen: false,
  theme: 'light',
};

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    addNotification: (state, action: PayloadAction<Omit<Notification, 'id'>>) => {
      const notification: Notification = {
        ...action.payload,
        id: Date.now().toString(),
        autoHide: action.payload.autoHide ?? true,
        duration: action.payload.duration ?? 5000,
      };
      state.notifications.push(notification);
    },
    removeNotification: (state, action: PayloadAction<string>) => {
      state.notifications = state.notifications.filter(
        (notification) => notification.id !== action.payload
      );
    },
    clearNotifications: (state) => {
      state.notifications = [];
    },
    setGlobalLoading: (state, action: PayloadAction<boolean>) => {
      state.isGlobalLoading = action.payload;
    },
    toggleSidebar: (state) => {
      state.sidebarOpen = !state.sidebarOpen;
    },
    setSidebarOpen: (state, action: PayloadAction<boolean>) => {
      state.sidebarOpen = action.payload;
    },
    toggleTheme: (state) => {
      state.theme = state.theme === 'light' ? 'dark' : 'light';
    },
    setTheme: (state, action: PayloadAction<'light' | 'dark'>) => {
      state.theme = action.payload;
    },
  },
});

export const {
  addNotification,
  removeNotification,
  clearNotifications,
  setGlobalLoading,
  toggleSidebar,
  setSidebarOpen,
  toggleTheme,
  setTheme,
} = uiSlice.actions;

export default uiSlice.reducer;
