import { TestBed } from '@angular/core/testing';

import { BulkLoad } from './bulk-load';

describe('BulkLoad', () => {
  let service: BulkLoad;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BulkLoad);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
