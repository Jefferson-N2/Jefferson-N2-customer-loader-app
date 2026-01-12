import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Observable, Subject, timer } from 'rxjs';
import { map, takeUntil, switchMap } from 'rxjs/operators';
import { HealthService } from '../../services/health';
import { HealthStatus } from '../../shared/constants';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, MatButtonModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header implements OnInit, OnDestroy {
  healthStatus$: Observable<string>;
  private destroy$ = new Subject<void>();

  constructor(private healthService: HealthService) {
    this.healthStatus$ = this.createHealthStatusObservable();
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createHealthStatusObservable(): Observable<string> {
    return timer(0, 10000).pipe(
      switchMap(() => this.healthService.getHealth()),
      map(health => health?.status || HealthStatus.DOWN),
      takeUntil(this.destroy$)
    );
    return new Observable<string>();
  }
}
