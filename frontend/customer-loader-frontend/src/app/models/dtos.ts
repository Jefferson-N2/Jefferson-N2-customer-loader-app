/**
 * DTO: Respuesta de carga masiva
 */
export interface BulkLoadResponse {
  processId: string;
  status: string; 
  successCount: number;
  errorCount: number;
  message: string;
}

/**
 * DTO: Proceso de carga masiva
 */
export interface BulkLoadProcess {
  id: number;
  processId: string;
  fileName: string;
  status: string;
  totalRecords: number;
  successfulCount: number;
  errorCount: number;
  processingDate: string;
  createdAt: string;
  updatedAt: string;
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
  processedAt: string; 
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
  processingDate: string; 
  clients?: ClientDetail[];
  errors?: BulkLoadError[];
}

/**
 * DTO: Error de carga
 */
export interface BulkLoadError {
  id?: number;
  processId?: string;
  lineNumber: number; 
  errorMessage: string;
  fieldName: string;
  createdAt?: string;
}

/**
 * DTO: Detalle de cliente
 */
export interface ClientDetail {
  id?: number;
  clientCode: string;
  idType: string; 
  idNumber: string;
  firstName: string;
  lastName: string;
  birthDate: string; 
  joinDate: string; 
  email: string;
  phoneNumber: string;
  processId?: string;
  account?: Account;
  firstPayment?: PaymentInfo;
}

/**
 * DTO: Información de Pago
 */
export interface PaymentInfo {
  paymentDate: string;
  amount: number;
  status: string;
}

/**
 * DTO: Pago de Nómina
 */
export interface PayrollPayment {
  id?: number;
  accountId?: number;
  paymentDate: string;
  amount: number;
  status?: string;
}

/**
 * DTO: Cuenta
 */
export interface Account {
  id?: number;
  accountNumber: string;
  payrollValue: number;
  status: string;
  balance?: number;
}

/**
 * DTO: Health check
 */
export interface HealthResponse {
  status: 'UP' | 'DOWN';
  timestamp: string; 
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
