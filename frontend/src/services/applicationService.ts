import { AxiosResponse } from 'axios';
import { apiService } from './api';
import { JobApplication, ApplicationStatus, PaginatedResponse } from '../types';

/**
 * Application service following SRP
 * - Single responsibility: Handle job application-related API calls
 */
export class ApplicationService {
  private readonly basePath = '/applications';

  async applyForJob(
    applicationData: any,
    resumeFile: File
  ): Promise<AxiosResponse<JobApplication>> {
    const formData = new FormData();
    formData.append('application', JSON.stringify(applicationData));
    formData.append('resume', resumeFile);

    return apiService.post<JobApplication>(this.basePath, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  async getApplicationById(id: number): Promise<AxiosResponse<JobApplication>> {
    return apiService.get<JobApplication>(`${this.basePath}/${id}`);
  }

  async getMyApplications(
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<JobApplication>>> {
    return apiService.get<PaginatedResponse<JobApplication>>(
      `${this.basePath}/my-applications?page=${page}&size=${size}`
    );
  }

  async getApplicationsForJob(
    jobId: number,
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<JobApplication>>> {
    return apiService.get<PaginatedResponse<JobApplication>>(
      `${this.basePath}/job/${jobId}?page=${page}&size=${size}`
    );
  }

  async getApplicationsForCompany(
    companyId: number,
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<JobApplication>>> {
    return apiService.get<PaginatedResponse<JobApplication>>(
      `${this.basePath}/company/${companyId}?page=${page}&size=${size}`
    );
  }

  async getApplicationsByStatus(
    status: ApplicationStatus,
    page = 0,
    size = 20
  ): Promise<AxiosResponse<PaginatedResponse<JobApplication>>> {
    return apiService.get<PaginatedResponse<JobApplication>>(
      `${this.basePath}/status/${status}?page=${page}&size=${size}`
    );
  }

  async updateApplicationStatus(
    applicationId: number,
    statusData: {
      status: ApplicationStatus;
      notes?: string;
      interviewDate?: string;
      interviewTime?: string;
      interviewLocation?: string;
      interviewType?: string;
      interviewNotes?: string;
    }
  ): Promise<AxiosResponse<JobApplication>> {
    return apiService.put<JobApplication>(
      `${this.basePath}/${applicationId}/status`,
      statusData
    );
  }

  async withdrawApplication(applicationId: number): Promise<AxiosResponse<void>> {
    return apiService.put<void>(`${this.basePath}/${applicationId}/withdraw`);
  }

  async getApplicationCountForJob(jobId: number): Promise<AxiosResponse<number>> {
    return apiService.get<number>(`${this.basePath}/stats/job/${jobId}`);
  }

  async getMyApplicationCount(): Promise<AxiosResponse<number>> {
    return apiService.get<number>(`${this.basePath}/stats/my-applications-count`);
  }

  async bulkUpdateApplications(
    jobId: number,
    status: ApplicationStatus,
    notes?: string
  ): Promise<AxiosResponse<void>> {
    const params = new URLSearchParams();
    params.append('status', status);
    if (notes) params.append('notes', notes);

    return apiService.put<void>(
      `${this.basePath}/job/${jobId}/bulk-update?${params.toString()}`
    );
  }
}

export const applicationService = new ApplicationService();
