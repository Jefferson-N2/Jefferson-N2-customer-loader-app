import { Component, Input, ChangeDetectionStrategy, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { BehaviorSubject, Subject } from 'rxjs';
import { trigger, transition, style, animate } from '@angular/animations';

/**
 * Tipo de notificación a mostrar
 */
export type NotificationType = 'error' | 'success' | 'warning' | 'info';

/**
 * Interfaz para configurar una notificación
 */
export interface Notification {
  message: string;
  type: NotificationType;
  duration?: number; // en milisegundos, 0 = no auto-hide
}

/**
 * Componente reutilizable para mostrar notificaciones flash
 *
 * Características:
 * - Soporta 4 tipos: error, success, warning, info
 * - Auto-cierre configurable
 * - Animaciones suave
 * - WCAG 2.1 AA accesible
 * - Material Design
 *
 * @example
 * ```html
 * <app-notification
 *   [message]="'Carga completada'"
 *   type="success"
 *   [duration]="3000"
 *   (onClose)="handleClose()"
 * ></app-notification>
 * ```
 */
@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './notification.html',
  styleUrl: './notification.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('slideIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ])
  ]
})
export class NotificationComponent implements OnInit, OnDestroy {
  /**
   * Mensaje a mostrar en la notificación
   */
  @Input() message: string = '';

  /**
   * Tipo de notificación: error, success, warning, info
   */
  @Input() type: NotificationType = 'info';

  /**
   * Duración en milisegundos antes de auto-cerrar (0 = no auto-cierre)
   */
  @Input() duration: number = 5000;

  /**
   * Observable que indica si la notificación está visible
   */
  readonly isVisible$ = new BehaviorSubject<boolean>(true);

  /**
   * Ícono según el tipo de notificación
   */
  readonly iconMap: Record<NotificationType, string> = {
    error: 'error_outline',
    success: 'check_circle',
    warning: 'warning',
    info: 'info'
  };

  /**
   * Rol ARIA según el tipo de notificación
   */
  readonly roleMap: Record<NotificationType, string> = {
    error: 'alert',
    success: 'status',
    warning: 'alert',
    info: 'status'
  };

  /**
   * Etiqueta ARIA según el tipo de notificación
   */
  readonly ariaLabelMap: Record<NotificationType, string> = {
    error: 'Notificación de error',
    success: 'Notificación de éxito',
    warning: 'Notificación de advertencia',
    info: 'Información'
  };

  private readonly destroy$ = new Subject<void>();
  private timeout: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
    // Auto-cerrar notificación después del tiempo especificado
    if (this.duration > 0) {
      this.timeout = setTimeout(() => {
        this.close();
      }, this.duration);
    }
  }

  /**
   * Cierra la notificación
   */
  close(): void {
    this.isVisible$.next(false);
    if (this.timeout) {
      clearTimeout(this.timeout);
    }
  }

  /**
   * Retorna el ícono para el tipo de notificación
   */
  getIcon(): string {
    return this.iconMap[this.type] || 'info';
  }

  /**
   * Retorna el rol ARIA para el tipo de notificación
   */
  getRole(): string {
    return this.roleMap[this.type] || 'status';
  }

  /**
   * Retorna la etiqueta ARIA para el tipo de notificación
   */
  getAriaLabel(): string {
    return this.ariaLabelMap[this.type] || 'Notificación';
  }

  ngOnDestroy(): void {
    if (this.timeout) {
      clearTimeout(this.timeout);
    }
    this.destroy$.next();
    this.destroy$.complete();
  }
}
