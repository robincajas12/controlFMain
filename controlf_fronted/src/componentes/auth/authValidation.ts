/**
 * Validaciones compartidas por los formularios de login y registro,
 * centralizadas aquí para mantener las reglas y los mensajes consistentes
 * entre ambos.
 */

/** Formato de correo razonable: valida forma, no existencia. */
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * @param email texto a validar
 * @returns `true` si tiene forma de correo electrónico
 */
export const isValidEmail = (email: string): boolean => EMAIL_REGEX.test(email.trim());

export interface PasswordRequirement {
  id: string;
  label: string;
  test: (value: string) => boolean;
}

/** Requisitos mínimos de una contraseña segura, exigidos por el backend. */
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

/**
 * @param value contraseña a evaluar
 * @returns cada requisito junto a si la contraseña actual lo cumple, para pintar una checklist en vivo
 */
export const evaluatePassword = (value: string): PasswordRequirementState[] =>
  PASSWORD_REQUIREMENTS.map((requirement) => ({ ...requirement, met: requirement.test(value) }));

/**
 * @param value contraseña a evaluar
 * @returns `true` si cumple todos los requisitos mínimos
 */
export const isPasswordStrong = (value: string): boolean =>
  PASSWORD_REQUIREMENTS.every((requirement) => requirement.test(value));
