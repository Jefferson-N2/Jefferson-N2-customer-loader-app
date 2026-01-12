import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil, switchMap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { ErrorService } from '../../services/error-service';
import { PaginatedResponse, BulkLoadError } from '../../models';

@Component({
  selector: 'app-errors',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './errors.html',
  styleUrl: './errors.scss',
})
export class Errors implements OnInit, OnDestroy {
  processId: string | null = null;
  errors$: Observable<PaginatedResponse<BulkLoadError>>;
  isLoading$ = new BehaviorSubject<boolean>(false);
  error$ = new BehaviorSubject<string | null>(null);

  displayedColumns: string[] = ['lineNumber', 'field', 'code', 'message', 'id'];
  pageSize = 10;
  pageSizeOptions = [5, 10, 25];
  currentPage = 0;

  private destroy$ = new Subject<void>();
  private errorsSubject$ = new BehaviorSubject<PaginatedResponse<BulkLoadError>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: this.pageSize,
    number: 0,
    empty: true
  });

  constructor(
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.errors$ = this.errorsSubject$.asObservable();
  }

  ngOnInit(): void {
    this.route.params
      .pipe(
        switchMap(params => {
          this.processId = params['processId'];
          if (!this.processId) {
            this.error$.next('ID de proceso no proporcionado');
            return of(null);
          }
          return this.loadErrorsInternal();
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadErrors(): void {
    if (this.processId) {
      this.loadErrorsInternal().pipe(takeUntil(this.destroy$)).subscribe();
    }
  }

  private loadErrorsInternal(): Observable<PaginatedResponse<BulkLoadError>> {
    if (!this.processId) {
      return of({
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: 0,
        number: 0,
        empty: true
      });
    }

    this.isLoading$.next(true);
    this.error$.next(null);

    return this.errorService.getErrors(this.processId, this.currentPage, this.pageSize)
      .pipe(
        takeUntil(this.destroy$),
        catchError(error => {
          this.isLoading$.next(false);
          const errorMsg = error?.error?.message || 'Error al cargar los errores';
          this.error$.next(errorMsg);
          return of({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: this.pageSize,
            number: 0,
            empty: true
          });
        })
      );
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;

    if (this.processId) {
      this.isLoading$.next(true);
      this.errorService.getErrors(this.processId, this.currentPage, this.pageSize)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (data: PaginatedResponse<BulkLoadError>) => {
            this.errorsSubject$.next(data);
            this.isLoading$.next(false);
          },
          error: (error: any) => {
            this.isLoading$.next(false);
            this.error$.next(error?.error?.message || 'Error al cargar página');
          }
        });
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}
