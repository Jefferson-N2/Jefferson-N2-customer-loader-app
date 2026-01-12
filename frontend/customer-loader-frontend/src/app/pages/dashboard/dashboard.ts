import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { BulkLoadComponent } from './components/bulk-load/bulk-load';

/**
 * Componente contenedor del dashboard
 * 
 * Actúa como layout principal con:
 * - Sidebar de navegación
 * - Área principal de contenido
 * - Router outlet para componentes hijo
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule, BulkLoadComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Dashboard {}
