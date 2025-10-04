const fetch = require('node-fetch');
const { CookieJar, Cookie } = require('tough-cookie');
const crypto = require('crypto');

(async () => {
  try {
    const base = 'http://localhost:8082';
    const jar = new CookieJar();

    // helper: update jar with Set-Cookie headers from response
    const storeSetCookies = async (res) => {
      const sc = res.headers.raw()['set-cookie'] || [];
      for (const cookieStr of sc) {
        await jar.setCookie(cookieStr, base);
      }
    };

    const fetchWithJar = async (url, opts = {}) => {
      // attach cookie header
      const cookieHeader = (await jar.getCookieString(base)) || '';
      opts.headers = opts.headers || {};
      if (cookieHeader) opts.headers['Cookie'] = cookieHeader;
      const r = await fetch(url, opts);
      await storeSetCookies(r);
      return r;
    };

    // compute sha256 hex of password
    const pw = '123456';
    const sha = crypto.createHash('sha256').update(pw, 'utf8').digest('hex');
    console.log('sha =', sha);

    // login
    const loginRes = await fetchWithJar(base + '/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pseudo: 'admin', mdp: sha }),
      redirect: 'manual'
    });
    console.log('login status', loginRes.status);
    const loginBody = await loginRes.text();
    try { console.log('login body', JSON.parse(loginBody)); } catch(e){ console.log('login body', loginBody); }

    // show cookies
    console.log('cookies after login:', (await jar.getCookies(base)).map(c => ({ key: c.key, value: c.value })));

    // remove access cookie (simulate expiry)
  // simulate expired access token by setting tmg_at with Max-Age=0
  await jar.setCookie('tmg_at=; Max-Age=0; Path=/', base);
  console.log('set expired tmg_at to simulate expired access token');

    // first /me attempt -> expect 401
  let meRes = await fetchWithJar(base + '/api/auth/me', { method: 'GET' });
    console.log('/api/auth/me status (first):', meRes.status);
    let meText = await meRes.text();
    try { console.log('/api/auth/me body (first):', JSON.parse(meText)); } catch(e){ console.log('/api/auth/me body (first):', meText); }

    if (meRes.status === 401) {
      console.log('Got 401, calling /api/auth/refresh');
  const refRes = await fetchWithJar(base + '/api/auth/refresh', { method: 'POST' });
      console.log('/api/auth/refresh status:', refRes.status);
      const refText = await refRes.text();
      try { console.log('/api/auth/refresh body:', JSON.parse(refText)); } catch(e){ console.log('/api/auth/refresh body:', refText); }

      // show cookies after refresh
      console.log('cookies after refresh:', (await jar.getCookies(base)).map(c => ({ key: c.key, value: c.value })));

      // retry /me
  meRes = await fetchWithJar(base + '/api/auth/me', { method: 'GET' });
      console.log('/api/auth/me status (after refresh):', meRes.status);
      meText = await meRes.text();
      try { console.log('/api/auth/me body (after refresh):', JSON.parse(meText)); } catch(e){ console.log('/api/auth/me body (after refresh):', meText); }
    } else {
      console.log('Unexpected status for first /me, not 401');
    }

  } catch (err) {
    console.error('Error in integration script', err);
    process.exit(1);
  }
})();
