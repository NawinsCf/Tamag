const fetch = require('node-fetch');
const { CookieJar } = require('tough-cookie');
const crypto = require('crypto');

(async () => {
  try {
    const base = 'http://localhost:8081';
    const jar = new CookieJar();

    const storeSetCookies = async (res) => {
      const sc = res.headers.raw()['set-cookie'] || [];
      for (const cookieStr of sc) {
        await jar.setCookie(cookieStr, base);
      }
    };

    const fetchWithJar = async (url, opts = {}) => {
      const cookieHeader = (await jar.getCookieString(base)) || '';
      opts.headers = opts.headers || {};
      if (cookieHeader) opts.headers['Cookie'] = cookieHeader;
      const r = await fetch(url, opts);
      await storeSetCookies(r);
      return r;
    };

    const pw = '123456';
    const sha = crypto.createHash('sha256').update(pw, 'utf8').digest('hex');
    console.log('sha =', sha);

    const loginRes = await fetchWithJar(base + '/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pseudo: 'admin', mdp: sha })
    });
    console.log('login status', loginRes.status);
    console.log('cookies after login:', (await jar.getCookies(base)).map(c => ({ key: c.key, value: c.value })));

    // choose tamago
    const choosePayload = { idtype: 1, nom: 'FromIntegrationTest' };
    const chooseRes = await fetchWithJar(base + '/api/user/1/choose-tamago', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(choosePayload)
    });
    console.log('choose status', chooseRes.status);
    const chooseText = await chooseRes.text();
    try { console.log('choose body', JSON.parse(chooseText)); } catch(e) { console.log('choose body', chooseText); }

  } catch (err) {
    console.error('Error', err);
    process.exit(1);
  }
})();
