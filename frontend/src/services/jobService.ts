import { AxiosResponse } from 'axios';
import { apiService } from './api';
import { Job, JobSearchFilters, PaginatedResponse } from '../types';

/**
 * Job service following SRP
 * - Single responsibility: Handle job-related API calls
 */
export class JobService {
  private readonly basePath = '/jobs';

  async getAllJobs(page = 0, size = 20): Promise<AxiosResponse<PaginatedResponse<Job>>> {
    return apiService.get<PaginatedResponse<Job>>(`${this.basePath}?page=${page}&size=${size}`);
  }

  async getJobById(id: number): Promise<AxiosResponse<Job>> {
    return apiService.get<Job>(`${this.basePath}/${id}`);
  }

  async searchJobs(
    filters: JobSearchFilters,
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<Job>>> {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());

    if (filters.query) params.append('query', filters.query);
    if (filters.location) params.append('location', filters.location);
    if (filters.jobType) params.append('jobType', filters.jobType);
    if (filters.experienceLevel) params.append('experienceLevel', filters.experienceLevel);
    if (filters.minSalary) params.append('minSalary', filters.minSalary.toString());
    if (filters.maxSalary) params.append('maxSalary', filters.maxSalary.toString());
    if (filters.isRemote !== undefined) params.append('isRemote', filters.isRemote.toString());
    if (filters.categoryId) params.append('categoryId', filters.categoryId.toString());

    const endpoint = filters.query 
      ? `${this.basePath}/search?${params.toString()}`
      : `${this.basePath}/filter?${params.toString()}`;

    return apiService.get<PaginatedResponse<Job>>(endpoint);
  }

  async getJobsByCompany(
    companyId: number,
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<Job>>> {
    return apiService.get<PaginatedResponse<Job>>(
      `${this.basePath}/company/${companyId}?page=${page}&size=${size}`
    );
  }

  async getJobsByCategory(
    categoryId: number,
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<Job>>> {
    return apiService.get<PaginatedResponse<Job>>(
      `${this.basePath}/category/${categoryId}?page=${page}&size=${size}`
    );
  }

  async getJobsBySkills(
    skillIds: number[],
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<Job>>> {
    const skillParams = skillIds.map(id => `skillIds=${id}`).join('&');
    return apiService.get<PaginatedResponse<Job>>(
      `${this.basePath}/skills?${skillParams}&page=${page}&size=${size}`
    );
  }

  async getRecommendedJobs(page = 0, size = 20): Promise<AxiosResponse<PaginatedResponse<Job>>> {
    return apiService.get<PaginatedResponse<Job>>(
      `${this.basePath}/recommendations?page=${page}&size=${size}`
    );
  }

  async createJob(jobData: any): Promise<AxiosResponse<Job>> {
    return apiService.post<Job>(this.basePath, jobData);
  }

  async updateJob(id: number, jobData: any): Promise<AxiosResponse<Job>> {
    return apiService.put<Job>(`${this.basePath}/${id}`, jobData);
  }

  async deactivateJob(id: number): Promise<AxiosResponse<void>> {
    return apiService.put<void>(`${this.basePath}/${id}/deactivate`);
  }

  async activateJob(id: number): Promise<AxiosResponse<void>> {
    return apiService.put<void>(`${this.basePath}/${id}/activate`);
  }

  async deleteJob(id: number): Promise<AxiosResponse<void>> {
    return apiService.delete<void>(`${this.basePath}/${id}`);
  }
}

export const jobService = new JobService();
