/**
 * Constantes globales de la aplicación
 */

export const APP_CONSTANTS = {
  TITLE: 'Customer Loader - Carga Masiva de Clientes',
  VERSION: '1.0.0',
};

/**
 * Estados de salud
 */
export enum HealthStatus {
  UP = 'UP',
  DOWN = 'DOWN',
  READY = 'READY',
  ALIVE = 'ALIVE'
}

/**
 * Estados de proceso
 */
export enum ProcessStatus {
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}

/**
 * Estados de carga de archivo
 */
export enum UploadStatus {
  IDLE = 'IDLE',
  UPLOADING = 'UPLOADING',
  SUCCESS = 'SUCCESS',
  ERROR = 'ERROR'
}

/**
 * Tipos de identificación
 */
export enum IdType {
  CEDULA = 'C',
  PASAPORTE = 'P'
}

/**
 * Mapeos de tipos de ID a nombres
 */
export const ID_TYPE_LABELS: Record<string, string> = {
  [IdType.CEDULA]: 'Cédula',
  [IdType.PASAPORTE]: 'Pasaporte'
};

/**
 * Mensajes de la aplicación
 */
export const MESSAGES = {
  UPLOAD_SUCCESS: 'Archivo cargado exitosamente',
  UPLOAD_ERROR: 'Error al cargar el archivo',
  UPLOAD_INVALID_SIZE: 'El archivo excede el tamaño máximo de 50MB',
  UPLOAD_INVALID_FORMAT: 'Solo se permiten archivos TXT',
  HEALTH_CHECK_ERROR: 'Error al obtener el estado de salud',
  LOADING_DATA: 'Cargando datos...',
  NO_DATA: 'No hay datos disponibles',
  ERROR_LOADING_DATA: 'Error al cargar los datos',
};

/**
 * Valores por defecto
 */
export const DEFAULTS = {
  PAGE_SIZE: 10,
  HEALTH_CHECK_INTERVAL: 20000, // 10 segundos
  REQUEST_TIMEOUT: 30000, // 30 segundos
  SNACKBAR_DURATION: 5000, // 5 segundos
};

/**
 * Configuración de tabla Material
 */
export const TABLE_CONFIG = {
  PAGE_SIZE_OPTIONS: [5, 10, 25, 50],
  DEFAULT_PAGE_SIZE: 10,
  DEFAULT_SORT_BY: 'id',
  DEFAULT_SORT_DIRECTION: 'desc' as const
};
