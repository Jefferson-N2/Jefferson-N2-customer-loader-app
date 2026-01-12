import { Component, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { BehaviorSubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { BulkLoadService } from '../../../../services/bulk-load';
import { NotificationComponent, NotificationType } from '../../../../shared/components/notification';
import { environment } from '../../../../../environments/environment';

/**
 * Componente para la carga de archivos TXT con clientes
 * 
 * Características:
 * - Soporte para drag & drop
 * - Validación de tipo y tamaño de archivo
 * - Barra de progreso durante la carga
 * - Mensajes de error accesibles
 * - Navegación automática a resultados
 */
@Component({
  selector: 'app-bulk-load',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    NotificationComponent
  ],
  templateUrl: './bulk-load.html',
  styleUrl: './bulk-load.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BulkLoadComponent implements OnDestroy {
  /** Observable del archivo seleccionado */
  readonly selectedFile$ = new BehaviorSubject<File | null>(null);
  
  /** Observable del estado de carga */
  readonly isUploading$ = new BehaviorSubject<boolean>(false);
  
  /** Observable del progreso de carga (0-100) */
  readonly uploadProgress$ = new BehaviorSubject<number>(0);
  
  /** Observable del mensaje de notificación */
  readonly notificationMessage$ = new BehaviorSubject<string | null>(null);
  
  /** Observable del tipo de notificación */
  readonly notificationType$ = new BehaviorSubject<NotificationType>('info');
  
  /** Indica si el área de drag está activa */
  isDragOver = false;

  private readonly destroy$ = new Subject<void>();
  private readonly maxFileSize = environment.uploadMaxSize;
  private readonly maxSizeMB = this.maxFileSize / (1024 * 1024);

  constructor(
    private readonly bulkLoadService: BulkLoadService,
    private readonly router: Router
  ) {}

  /**
   * Maneja la selección de archivo del input
   * 
   * @param event - Evento del input file
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.processFile(input.files[0]);
    }
  }

  /**
   * Maneja el evento de arrastre sobre el área
   * 
   * @param event - Evento de drag over
   */
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  /**
   * Maneja cuando el archivo sale del área de arrastre
   */
  onDragLeave(): void {
    this.isDragOver = false;
  }

  /**
   * Maneja el drop de archivo
   * 
   * @param event - Evento de drop
   */
  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;

    if (event.dataTransfer?.files?.length) {
      this.processFile(event.dataTransfer.files[0]);
    }
  }

  /**
   * Valida y procesa el archivo seleccionado
   * 
   * @private
   * @param file - Archivo a procesar
   */
  private processFile(file: File): void {
    // Validar extensión
    if (!file.name.endsWith('.txt')) {
      this.showNotification('El archivo debe ser de formato TXT', 'error');
      return;
    }

    // Validar tamaño
    if (file.size > this.maxFileSize) {
      this.showNotification(
        `El archivo excede el tamaño máximo de ${Math.round(this.maxSizeMB)}MB`,
        'error'
      );
      return;
    }

    // Archivo válido
    this.clearNotification();
    this.selectedFile$.next(file);
  }

  /**
   * Inicia la carga del archivo al servidor
   */
  uploadFile(): void {
    const file = this.selectedFile$.value;
    if (!file) {
      return;
    }

    this.isUploading$.next(true);
    this.uploadProgress$.next(0);

    this.bulkLoadService
      .uploadClients(file)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.handleUploadSuccess(response);
        },
        error: (error) => {
          this.handleUploadError(error);
        }
      });
  }

  /**
   * Limpia la selección del archivo
   */
  clearFile(): void {
    this.selectedFile$.next(null);
    this.uploadProgress$.next(0);
    this.clearNotification();
  }

  /**
   * Maneja la carga exitosa
   * 
   * @private
   */
  private handleUploadSuccess(response: any): void {
    this.isUploading$.next(false);
    this.uploadProgress$.next(100);

    const message = `✓ Carga completada: ${response.successCount} clientes creados`;
    this.showNotification(message, 'success');

    // Navegar a resultados después de mostrar el mensaje
    setTimeout(() => {
      this.router.navigate(['/dashboard/clients', response.processId]);
    }, 2000);
  }

  /**
   * Maneja errores en la carga
   * 
   * @private
   */
  private handleUploadError(error: any): void {
    this.isUploading$.next(false);
    let errorMsg = 'Error al cargar el archivo';
    
    // Extraer mensaje de error más específico
    if (error?.message) {
      errorMsg = error.message;
    } else if (error?.error?.message) {
      errorMsg = error.error.message;
    }
    
    this.showNotification(errorMsg, 'error');
  }

  /**
   * Muestra una notificación al usuario
   * 
   * @private
   */
  private showNotification(message: string, type: NotificationType): void {
    this.notificationMessage$.next(message);
    this.notificationType$.next(type);
  }

  /**
   * Limpia la notificación actual
   * 
   * @private
   */
  private clearNotification(): void {
    this.notificationMessage$.next(null);
  }

  /**
   * Convierte bytes a formato legible
   * 
   * @param bytes - Tamaño en bytes
   * @returns Tamaño formateado (ej: "2.5 MB")
   * 
   * @example
   * ```typescript
   * this.getFileSize(1048576) // "1 MB"
   * ```
   */
  getFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return (
      Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
