export interface Direccion {
  calle: string;
  ciudad: string;
  referencia?: string;
}

export interface CrearEnvioRequest {
  paquete: { descripcion: string; pesoKg: number };
  origen: Direccion;
  destino: Direccion;
  receptor: { nombre: string; telefono: string; email?: string };
}

export interface EnvioResponse {
  id: string;
  codigoSeguimiento: string;
  estado: string;   // se espera "CREADO"
}