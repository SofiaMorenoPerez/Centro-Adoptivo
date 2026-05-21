import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Recibir } from './recibir';

describe('Recibir', () => {
  let component: Recibir;
  let fixture: ComponentFixture<Recibir>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Recibir],
    }).compileComponents();

    fixture = TestBed.createComponent(Recibir);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
