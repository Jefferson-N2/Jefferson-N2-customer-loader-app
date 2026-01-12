import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BehaviorSubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { BulkLoadService } from '../../../../services/bulk-load';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-bulk-load',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatSnackBarModule
  ],
  templateUrl: './bulk-load.html',
  styleUrl: './bulk-load.scss',
})
export class BulkLoadComponent implements OnInit, OnDestroy {
  selectedFile$ = new BehaviorSubject<File | null>(null);
  isUploading$ = new BehaviorSubject<boolean>(false);
  uploadProgress$ = new BehaviorSubject<number>(0);
  errorMessage$ = new BehaviorSubject<string | null>(null);
  isDragOver = false;

  private destroy$ = new Subject<void>();
  private maxFileSize = environment.uploadMaxSize;

  constructor(
    private bulkLoadService: BulkLoadService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.processFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  onDragLeave(): void {
    this.isDragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;

    if (event.dataTransfer?.files?.length) {
      this.processFile(event.dataTransfer.files[0]);
    }
  }

  private processFile(file: File): void {
    if (!file.name.endsWith('.txt')) {
      this.setError('El archivo debe ser de formato TXT');
      return;
    }

    if (file.size > this.maxFileSize) {
      const maxSizeMB = this.maxFileSize / (1024 * 1024);
      this.setError(`El archivo excede el tamaño máximo de ${maxSizeMB}MB`);
      return;
    }

    this.clearError();
    this.selectedFile$.next(file);
  }

  uploadFile(): void {
    const file = this.selectedFile$.value;
    if (!file) return;

    this.isUploading$.next(true);
    this.uploadProgress$.next(0);

    this.bulkLoadService
      .uploadClients(file)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.isUploading$.next(false);
          this.uploadProgress$.next(100);
          
          this.snackBar.open(
            `✓ Carga completada: ${response.successCount} clientes creados`,
            'Cerrar',
            { duration: 5000 }
          );

          setTimeout(() => {
            this.router.navigate(['/dashboard/clients', response.processId]);
          }, 500);
        },
        error: (error) => {
          this.isUploading$.next(false);
          const errorMsg = error?.error?.message || 'Error al cargar el archivo';
          this.setError(errorMsg);
          this.snackBar.open(`✗ ${errorMsg}`, 'Cerrar', { duration: 5000 });
        }
      });
  }

  clearFile(): void {
    this.selectedFile$.next(null);
    this.uploadProgress$.next(0);
    this.clearError();
  }

  private setError(message: string): void {
    this.errorMessage$.next(message);
  }

  private clearError(): void {
    this.errorMessage$.next(null);
  }

  getFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  }
}
