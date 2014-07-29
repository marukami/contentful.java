package com.contentful.java;

import com.contentful.java.lib.NyanCat;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Test of all Entries fetching methods via {@link CDAClient}.
 */
public class EntriesTest extends AbsTestCase {
    private RetrofitError retrofitError;

    public void testFetchEntries() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);

        retrofitError = null;

        client.fetchEntries(new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onFailure(RetrofitError retrofitError) {
                EntriesTest.this.retrofitError = retrofitError;
                super.onFailure(retrofitError);
            }
        });

        cdl.await();

        assertNull(retrofitError);
    }

    public void testFetchEntriesMatching() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);

        retrofitError = null;

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("sys.id", "nyancat");

        client.fetchEntriesMatching(query, new TestCallback<CDAListResult>(cdl) {
            @Override
            protected void onFailure(RetrofitError retrofitError) {
                EntriesTest.this.retrofitError = retrofitError;
                super.onFailure(retrofitError);
            }
        });

        cdl.await();

        assertNull(retrofitError);
    }

    public void testFetchEntryWithIdentifier() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);

        retrofitError = null;

        client.fetchEntryWithIdentifier("nyancat", new TestCallback<CDAEntry>(cdl) {
            @Override
            protected void onFailure(RetrofitError retrofitError) {
                EntriesTest.this.retrofitError = retrofitError;
                super.onFailure(retrofitError);
            }
        });

        cdl.await();

        assertNull(retrofitError);
    }

    public void testFetchEntryOfCustomClass() throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1);

        final Object[] result = {null};
        retrofitError = null;

        // use a new client instance
        CDAClient customClient = TestClientFactory.newInstance();

        // register custom class
        customClient.registerCustomClass("cat", NyanCat.class);

        customClient.fetchEntryWithIdentifier("nyancat", new TestCallback<NyanCat>(cdl) {
            @Override
            protected void onSuccess(NyanCat nyanCat, Response response) {
                result[0] = nyanCat;
                super.onSuccess(nyanCat, response);
            }
        });

        cdl.await();

        assertNull(retrofitError);
        assertEquals(result.length, 1);
        assertTrue(result[0] instanceof NyanCat);

        NyanCat cat = (NyanCat) result[0];

        // name
        assertTrue("Nyan Cat".equals(cat.fields.name));

        // color
        assertTrue("rainbow".equals(cat.fields.color));

        // lives
        assertTrue(Integer.valueOf(1337).equals(cat.fields.lives));

        // likes
        assertNotNull(cat.fields.likes);
        assertTrue(cat.fields.likes.size() == 2);
        assertTrue(cat.fields.likes.contains("rainbows"));
        assertTrue(cat.fields.likes.contains("fish"));

        // birthday
        assertEquals(new Date(1301954400000L), cat.fields.birthday);
    }
}