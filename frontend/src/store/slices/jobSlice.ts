import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Job, JobSearchFilters, PaginatedResponse } from '../../types';
import { jobService } from '../../services/jobService';

/**
 * Job slice following SRP
 * - Single responsibility: Manage job-related state
 */

interface JobState {
  jobs: Job[];
  currentJob: Job | null;
  recommendedJobs: Job[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  isLoading: boolean;
  error: string | null;
  filters: JobSearchFilters;
}

const initialState: JobState = {
  jobs: [],
  currentJob: null,
  recommendedJobs: [],
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  isLoading: false,
  error: null,
  filters: {},
};

// Async thunks
export const fetchJobsAsync = createAsyncThunk(
  'jobs/fetchJobs',
  async ({ page = 0, size = 20 }: { page?: number; size?: number }, { rejectWithValue }) => {
    try {
      const response = await jobService.getAllJobs(page, size);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch jobs');
    }
  }
);

export const searchJobsAsync = createAsyncThunk(
  'jobs/searchJobs',
  async (
    { filters, page = 0, size = 20 }: { filters: JobSearchFilters; page?: number; size?: number },
    { rejectWithValue }
  ) => {
    try {
      const response = await jobService.searchJobs(filters, page, size);
      return { data: response.data, filters };
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to search jobs');
    }
  }
);

export const fetchJobByIdAsync = createAsyncThunk(
  'jobs/fetchJobById',
  async (jobId: number, { rejectWithValue }) => {
    try {
      const response = await jobService.getJobById(jobId);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch job');
    }
  }
);

export const fetchRecommendedJobsAsync = createAsyncThunk(
  'jobs/fetchRecommendedJobs',
  async ({ page = 0, size = 20 }: { page?: number; size?: number }, { rejectWithValue }) => {
    try {
      const response = await jobService.getRecommendedJobs(page, size);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch recommended jobs');
    }
  }
);

const jobSlice = createSlice({
  name: 'jobs',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentJob: (state) => {
      state.currentJob = null;
    },
    updateFilters: (state, action: PayloadAction<JobSearchFilters>) => {
      state.filters = { ...state.filters, ...action.payload };
    },
    clearFilters: (state) => {
      state.filters = {};
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch jobs
      .addCase(fetchJobsAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchJobsAsync.fulfilled, (state, action: PayloadAction<PaginatedResponse<Job>>) => {
        state.isLoading = false;
        state.jobs = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.totalPages = action.payload.totalPages;
        state.currentPage = action.payload.number;
      })
      .addCase(fetchJobsAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      
      // Search jobs
      .addCase(searchJobsAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(searchJobsAsync.fulfilled, (state, action) => {
        state.isLoading = false;
        state.jobs = action.payload.data.content;
        state.totalElements = action.payload.data.totalElements;
        state.totalPages = action.payload.data.totalPages;
        state.currentPage = action.payload.data.number;
        state.filters = action.payload.filters;
      })
      .addCase(searchJobsAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      
      // Fetch job by ID
      .addCase(fetchJobByIdAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchJobByIdAsync.fulfilled, (state, action: PayloadAction<Job>) => {
        state.isLoading = false;
        state.currentJob = action.payload;
      })
      .addCase(fetchJobByIdAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      
      // Fetch recommended jobs
      .addCase(fetchRecommendedJobsAsync.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchRecommendedJobsAsync.fulfilled, (state, action: PayloadAction<PaginatedResponse<Job>>) => {
        state.isLoading = false;
        state.recommendedJobs = action.payload.content;
      })
      .addCase(fetchRecommendedJobsAsync.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearError, clearCurrentJob, updateFilters, clearFilters } = jobSlice.actions;
export default jobSlice.reducer;
