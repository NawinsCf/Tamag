const fetch = require('node-fetch');
const { CookieJar } = require('tough-cookie');
const crypto = require('crypto');

(async () => {
  try {
    const authBase = 'http://localhost:8082'; // tamagoservice
    const feedBase = 'http://localhost:8081'; // feedservice
    const jar = new CookieJar();

    const storeSetCookies = async (res, url) => {
      const sc = res.headers.raw()['set-cookie'] || [];
      for (const cookieStr of sc) {
        await jar.setCookie(cookieStr, url);
      }
    };

    const fetchWithJar = async (url, opts = {}, cookieUrl) => {
      const target = cookieUrl || url;
      const cookieHeader = (await jar.getCookieString(target)) || '';
      opts.headers = opts.headers || {};
      if (cookieHeader) opts.headers['Cookie'] = cookieHeader;
      const r = await fetch(url, opts);
      // store Set-Cookie using the target origin for proper host mapping
      await storeSetCookies(r, target);
      return r;
    };

    const pw = '123456';
    const sha = crypto.createHash('sha256').update(pw, 'utf8').digest('hex');
    console.log('sha =', sha);

    // login on tamagoservice
    const loginRes = await fetchWithJar(authBase + '/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pseudo: 'admin', mdp: sha })
    }, authBase);
    console.log('login status', loginRes.status);
    console.log('cookies after login (auth):', (await jar.getCookies(authBase)).map(c => ({ key: c.key, value: c.value })));

    // Now call feedservice with cookies copied from auth origin (simulate same-origin proxy)
    const choosePayload = { idtype: 1, nom: 'FromCrossHost' };
    // Build Cookie header from authBase cookies
    const authCookies = (await jar.getCookies(authBase)).map(c => `${c.key}=${c.value}`).join('; ');
    const chooseRes = await fetchWithJar(feedBase + '/api/user/1/choose-tamago', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Cookie': authCookies },
      body: JSON.stringify(choosePayload)
    }, feedBase);

    console.log('choose status', chooseRes.status);
    const chooseText = await chooseRes.text();
    try { console.log('choose body', JSON.parse(chooseText)); } catch(e) { console.log('choose body', chooseText); }

  } catch (err) {
    console.error('Error', err);
    process.exit(1);
  }
})();
