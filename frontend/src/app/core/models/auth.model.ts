// Coincide con LoginRequestDto del backend
export interface LoginRequest {
  email: string;
  password: string;
}

// Coincide con RegisterRequestDto del backend
export interface RegistroRequest {
  fullName: string;
  email: string;
  password: string;
}

// Coincide con AuthResponseDto del backend
export interface AuthResponse {
  token: string;
  email: string;
  fullName: string;
  role: string;   // "USER" | "ADMIN"
}
