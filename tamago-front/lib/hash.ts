export async function sha256Hex(input: string): Promise<string> {
  const enc = new TextEncoder();
  const data = enc.encode(input);

  // Preferred: Web Crypto API (available in secure browser contexts)
  if (typeof globalThis?.crypto?.subtle?.digest === 'function') {
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  }

  // Fallback 1: dynamic import of js-sha256 (small pure-JS implementation)
  try {
    const mod = await import('js-sha256');
    const sha = (mod.default ?? (mod as any).sha256 ?? mod) as any;
    if (typeof sha === 'function') {
      return sha(input);
    }
  } catch (e) {
    // ignore and try next fallback
  }

  // Fallback 2: Node's crypto (when running in Node environments)
  try {
    // dynamic import to avoid bundling Node-only module into browser bundle
    // @ts-ignore
    const nodeCrypto = await import('crypto');
    if (nodeCrypto && typeof nodeCrypto.createHash === 'function') {
      return nodeCrypto.createHash('sha256').update(input, 'utf8').digest('hex');
    }
  } catch (e) {
    // ignore
  }

  throw new Error('No SHA-256 implementation available: ensure the app runs in a secure (HTTPS) browser context or install the `js-sha256` package as a fallback.');
}
