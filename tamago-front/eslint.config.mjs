import { dirname } from "path";
import { fileURLToPath } from "url";
import { FlatCompat } from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
  baseDirectory: __dirname,
});

const eslintConfig = [
  ...compat.extends("next/core-web-vitals", "next/typescript"),
  {
    ignores: [
      "node_modules/**",
      ".next/**",
      "out/**",
      "build/**",
      "next-env.d.ts",
    ],
    rules: {
      // Temporarily allow explicit any in the codebase while migrating to JWT cookies.
      '@typescript-eslint/no-explicit-any': 'off',
      // keep other rules strict but warn about unused vars
      '@typescript-eslint/no-unused-vars': ['warn', { 'argsIgnorePattern': '^_' }],
    }
  },
];

export default eslintConfig;
