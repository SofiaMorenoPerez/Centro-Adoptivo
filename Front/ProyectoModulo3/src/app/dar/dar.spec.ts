import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Dar } from './dar';

describe('Dar', () => {
  let component: Dar;
  let fixture: ComponentFixture<Dar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Dar],
    }).compileComponents();

    fixture = TestBed.createComponent(Dar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
