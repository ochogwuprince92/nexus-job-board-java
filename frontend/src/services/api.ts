import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { AuthResponse, LoginRequest, RegisterRequest } from '../types';

/**
 * API service following SOLID principles
 * - SRP: Handles only HTTP communication
 * - OCP: Open for extension with new endpoints
 * - DIP: Depends on axios abstraction
 */

class ApiService {
  private api: AxiosInstance;
  private baseURL: string;

  constructor() {
    this.baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';
    
    this.api = axios.create({
      baseURL: this.baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor to add auth token
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor to handle token refresh
    this.api.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
              const response = await this.refreshToken(refreshToken);
              const { accessToken } = response.data;
              
              localStorage.setItem('accessToken', accessToken);
              originalRequest.headers.Authorization = `Bearer ${accessToken}`;
              
              return this.api(originalRequest);
            }
          } catch (refreshError) {
            // Refresh failed, redirect to login
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '/login';
          }
        }

        return Promise.reject(error);
      }
    );
  }

  // Generic HTTP methods
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.api.get<T>(url, config);
  }

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.api.post<T>(url, data, config);
  }

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.api.put<T>(url, data, config);
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.api.delete<T>(url, config);
  }

  // Authentication endpoints
  async login(credentials: LoginRequest): Promise<AxiosResponse<AuthResponse>> {
    return this.post<AuthResponse>('/auth/login', credentials);
  }

  async register(userData: RegisterRequest): Promise<AxiosResponse<any>> {
    return this.post('/auth/register', userData);
  }

  async refreshToken(refreshToken: string): Promise<AxiosResponse<AuthResponse>> {
    return this.post<AuthResponse>('/auth/refresh', { refreshToken });
  }

  async logout(): Promise<AxiosResponse<void>> {
    return this.post<void>('/auth/logout');
  }

  // File upload helper
  async uploadFile(url: string, file: File, additionalData?: any): Promise<AxiosResponse<any>> {
    const formData = new FormData();
    formData.append('file', file);
    
    if (additionalData) {
      Object.keys(additionalData).forEach(key => {
        formData.append(key, additionalData[key]);
      });
    }

    return this.api.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  // Get base URL for file downloads
  getFileUrl(relativePath: string): string {
    return `${this.baseURL}${relativePath}`;
  }
}

// Export singleton instance
export const apiService = new ApiService();
export default apiService;
