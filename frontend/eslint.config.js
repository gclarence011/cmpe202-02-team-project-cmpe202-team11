// @ts-check

import js from "@eslint/js";
import globals from "globals";
import react from "eslint-plugin-react";
import reactHooks from "eslint-plugin-react-hooks";
import prettier from "eslint-config-prettier";
import eslintPluginImport from "eslint-plugin-import";

/**
 * ESLint configuration for Campus Marketplace Frontend
 * - Compatible with React + Vite
 * - Works with JS or TS files
 * - Integrates Prettier (no formatting conflicts)
 */

export default [
  js.configs.recommended,
  {
    files: ["**/*.{js,jsx,ts,tsx}"],
    ignores: ["dist/**", "node_modules/**"],

    languageOptions: {
      globals: globals.browser,
      parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        ecmaFeatures: { jsx: true },
      },
    },

    plugins: {
      react,
      "react-hooks": reactHooks,
      import: eslintPluginImport,
    },

    rules: {
      // --- React Rules ---
      "react/react-in-jsx-scope": "off", // Vite handles React import automatically
      "react/prop-types": "off",
      "react-hooks/rules-of-hooks": "error",
      "react-hooks/exhaustive-deps": "warn",

      // --- Import Rules ---
      "import/order": [
        "warn",
        {
          groups: ["builtin", "external", "internal", "parent", "sibling", "index"],
          "newlines-between": "always",
        },
      ],

      // --- Code Quality ---
      "no-unused-vars": ["warn", { argsIgnorePattern: "^_" }],
      "no-console": "off",
      "no-debugger": "warn",

      // --- Style Consistency ---
      "prefer-const": "warn",
      "no-var": "error",

      // --- Prettier Integration ---
      ...prettier.rules,
    },

    settings: {
      react: { version: "detect" },
    },
  },
];
