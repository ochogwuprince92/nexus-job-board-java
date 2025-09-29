import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { JobApplication, PaginatedResponse } from '../../types';
import { applicationService } from '../../services/applicationService';

/**
 * Application slice following SRP
 * - Single responsibility: Manage job application state
 */

interface ApplicationState {
  applications: JobApplication[];
  currentApplication: JobApplication | null;
  totalElements: number;
  totalPages: number;
  currentPage: number;
  isLoading: boolean;
  error: string | null;
}

const initialState: ApplicationState = {
  applications: [],
  currentApplication: null,
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  isLoading: false,
  error: null,
};

// Async thunks
export const fetchMyApplicationsAsync = createAsyncThunk(
  'applications/fetchMyApplications',
  async ({ page = 0, size = 20 }: { page?: number; size?: number }, { rejectWithValue }) => {
    try {
      const response = await applicationService.getMyApplications(page, size);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch applications');
    }
  }
);

export const fetchApplicationByIdAsync = createAsyncThunk(
  'applications/fetchApplicationById',
  async (applicationId: number, { rejectWithValue }) => {
    try {
      const response = await applicationService.getApplicationById(applicationId);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch application');
    }
  }
);

export const applyForJobAsync = createAsyncThunk(
  'applications/applyForJob',
  async (
    { applicationData, resumeFile }: { applicationData: any; resumeFile: File },
    { rejectWithValue }
  ) => {
    try {
      const response = await applicationService.applyForJob(applicationData, resumeFile);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to submit application');
    }
  }
);

export const withdrawApplicationAsync = createAsyncThunk(
  'applications/withdrawApplication',
  async (applicationId: number, { rejectWithValue }) => {
    try {
      await applicationService.withdrawApplication(applicationId);
      return applicationId;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to withdraw application');
    }
  }
);

const applicationSlice = createSlice({
  name: 'applications',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentApplication: (state) => {
      state.currentApplication = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch my applications
      .addCase(fetchMyApplicationsAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchMyApplicationsAsync.fulfilled, (state, action: PayloadAction<PaginatedResponse<JobApplication>>) => {
        state.isLoading = false;
        state.applications = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.totalPages = action.payload.totalPages;
        state.currentPage = action.payload.number;
      })
      .addCase(fetchMyApplicationsAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      
      // Fetch application by ID
      .addCase(fetchApplicationByIdAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchApplicationByIdAsync.fulfilled, (state, action: PayloadAction<JobApplication>) => {
        state.isLoading = false;
        state.currentApplication = action.payload;
      })
      .addCase(fetchApplicationByIdAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      
      // Apply for job
      .addCase(applyForJobAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(applyForJobAsync.fulfilled, (state, action: PayloadAction<JobApplication>) => {
        state.isLoading = false;
        state.applications.unshift(action.payload);
      })
      .addCase(applyForJobAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      
      // Withdraw application
      .addCase(withdrawApplicationAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(withdrawApplicationAsync.fulfilled, (state, action: PayloadAction<number>) => {
        state.isLoading = false;
        const applicationId = action.payload;
        state.applications = state.applications.map(app =>
          app.id === applicationId
            ? { ...app, status: 'WITHDRAWN' as any }
            : app
        );
      })
      .addCase(withdrawApplicationAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearError, clearCurrentApplication } = applicationSlice.actions;
export default applicationSlice.reducer;
