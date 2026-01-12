/**
 * DTO: Respuesta de carga masiva
 */
export interface BulkLoadResponse {
  processId: string;
  status: string; // "COMPLETED", "PROCESSING", "FAILED"
  successCount: number;
  errorCount: number;
  message: string;
}

/**
 * DTO: Estadísticas del proceso
 */
export interface BulkLoadStatistics {
  processId: string;
  successfulCount: number;
  errorCount: number;
  totalCount: number;
  message: string;
  processedAt: string; // ISO 8601
}

/**
 * DTO: Detalles completos del proceso
 */
export interface ProcessDetails {
  processId: string;
  fileName: string;
  status: string;
  totalRecords: number;
  successfulCount: number;
  errorCount: number;
  processingDate: string; // ISO 8601
  clients?: ClientDetail[];
  errors?: BulkLoadError[];
}

/**
 * DTO: Error de carga
 */
export interface BulkLoadError {
  code: string; // "VALIDATION_ERROR_EMAIL", etc
  message: string;
  field?: string; // Campo que falló
  lineNumber: number; // Línea del archivo
  idType?: string;
  idNumber?: string;
}

/**
 * DTO: Detalle de cliente
 */
export interface ClientDetail {
  clientCode: string;
  idType: string; // "C" o "P"
  idNumber: string;
  firstNames: string;
  lastNames: string;
  birthDate: string; // "YYYY-MM-DD"
  joinDate: string; // "YYYY-MM-DD"
  email: string;
  phoneNumber: string;
  account?: Account;
}

/**
 * DTO: Cuenta
 */
export interface Account {
  accountNumber: string;
  payrollValue: number;
  status: string; // "ACTIVE"
}

/**
 * DTO: Health check
 */
export interface HealthResponse {
  status: 'UP' | 'DOWN';
  timestamp: string; // ISO 8601
  service: string;
  version: string;
  checks?: {
    database: 'UP' | 'DOWN';
    [key: string]: string;
  };
}

/**
 * Wrapper para respuestas paginadas
 */
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  empty: boolean;
}
