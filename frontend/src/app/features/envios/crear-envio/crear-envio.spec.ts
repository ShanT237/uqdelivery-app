import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearEnvio } from './crear-envio';

describe('CrearEnvio', () => {
  let component: CrearEnvio;
  let fixture: ComponentFixture<CrearEnvio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrearEnvio],
    }).compileComponents();

    fixture = TestBed.createComponent(CrearEnvio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
