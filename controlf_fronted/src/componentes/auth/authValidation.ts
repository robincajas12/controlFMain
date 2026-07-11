/**
 * Validaciones compartidas por los formularios de inicio de sesión y registro.
 * Se centralizan aquí para reutilizarlas y mantener mensajes consistentes.
 */

// Formato de correo razonable (no exige que exista, solo que tenga forma válida).
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const isValidEmail = (email: string): boolean => EMAIL_REGEX.test(email.trim());

export interface PasswordRequirement {
  id: string;
  label: string;
  test: (value: string) => boolean;
}

// Requisitos mínimos de una contraseña segura solicitados por el sistema.
export const PASSWORD_REQUIREMENTS: PasswordRequirement[] = [
  { id: 'length', label: 'Al menos 10 caracteres', test: (v) => v.length >= 10 },
  { id: 'upper', label: 'Una letra mayúscula', test: (v) => /[A-Z]/.test(v) },
  { id: 'lower', label: 'Una letra minúscula', test: (v) => /[a-z]/.test(v) },
  { id: 'number', label: 'Un número', test: (v) => /[0-9]/.test(v) },
  { id: 'special', label: 'Un carácter especial (!@#$…)', test: (v) => /[^A-Za-z0-9]/.test(v) },
];

export interface PasswordRequirementState extends PasswordRequirement {
  met: boolean;
}

export const evaluatePassword = (value: string): PasswordRequirementState[] =>
  PASSWORD_REQUIREMENTS.map((requirement) => ({ ...requirement, met: requirement.test(value) }));

export const isPasswordStrong = (value: string): boolean =>
  PASSWORD_REQUIREMENTS.every((requirement) => requirement.test(value));
