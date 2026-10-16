const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');

test('Firestore Security Rules syntax validation', () => {
  const rules = fs.readFileSync('firestore.rules', 'utf8');
  assert.ok(rules.includes("rules_version = '2';"), 'Rules version must be 2');
  assert.ok(rules.includes('service cloud.firestore'), 'Must specify cloud.firestore');
  assert.ok(rules.includes('match /databases/{database}/documents'), 'Must match database documents root');
  assert.ok(rules.includes('request.auth.uid == userId'), 'Must enforce authenticated user ownership');
  assert.ok(!rules.includes('allow read, write: if true;'), 'Must never have open access');
});
