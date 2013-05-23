package org.apache.tapestry5.internal.services.assets

import org.apache.tapestry5.Asset
import org.apache.tapestry5.internal.test.InternalBaseTestCase
import org.apache.tapestry5.ioc.Resource
import org.apache.tapestry5.services.AssetSource
import org.testng.annotations.Test


class CSSURLRewriterTests extends InternalBaseTestCase {

    @Test
    void no_urls_in_content_returns_null() {
        def input = '''
body {
  color: red;
}
'''

        def rewriter = new CSSURLRewriter(null, null, null, null)

        assertNull rewriter.replaceURLs(input, null)
    }

    @Test
    void simple_replacement() {
        def input = '''
body {
  background: white url("images/back.png") attach-x;
}
'''

        def assetSource = newMock AssetSource
        def resource = newMock Resource
        def asset = newMock Asset

        expect(
            assetSource.getAsset(resource, "images/back.png", null)
        ).andReturn asset

        expect(asset.toClientURL()).andReturn "/ctx/images/back.png"

        replay()


        def rewriter = new CSSURLRewriter(null, null, assetSource, null)

        def output = rewriter.replaceURLs input, resource

        assertEquals output, '''
body {
  background: white url("/ctx/images/back.png") attach-x;
}
'''

        verify()

    }


    // See TAP5-2106
    @Test
    void query_parameters_in_relative_url_are_maintained() {
        def input = '''
body {
  background: white url("images/back.png?v=1.0.0") attach-x;
}
'''

        def assetSource = newMock AssetSource
        def resource = newMock Resource
        def asset = newMock Asset

        expect(
            assetSource.getAsset(resource, "images/back.png", null)
        ).andReturn asset

        expect(asset.toClientURL()).andReturn "/ctx/images/back.png"

        replay()

        def rewriter = new CSSURLRewriter(null, null, assetSource, null)

        def output = rewriter.replaceURLs input, resource

        assertEquals output, '''
body {
  background: white url("/ctx/images/back.png?v=1.0.0") attach-x;
}
'''


    }

    @Test
    void unquoted_urls_are_matched() {
        def input = '''
body {
  background: white url(images/back.png) attach-x;
}
'''

        def assetSource = newMock AssetSource
        def resource = newMock Resource
        def asset = newMock Asset

        expect(
            assetSource.getAsset(resource, "images/back.png", null)
        ).andReturn asset

        expect(asset.toClientURL()).andReturn "/ctx/images/back.png"

        replay()


        def rewriter = new CSSURLRewriter(null, null, assetSource, null)

        def output = rewriter.replaceURLs input, resource

        assertEquals output, '''
body {
  background: white url("/ctx/images/back.png") attach-x;
}
'''

        verify()

    }

    @Test
    void absolute_urls_not_replaced() {
        def input = '''
body {
  background: white url("/images/back.png") attach-x;
}
'''

        def rewriter = new CSSURLRewriter(null, null, null, null)

        assertNull rewriter.replaceURLs(input, null)
    }

    @Test
    void complete_urls_are_not_replaced() {
        def input = '''
body {
  background: white url("data:image/png;base64,CODE64A") attach-x;
}
'''

        def rewriter = new CSSURLRewriter(null, null, null, null)

        assertNull rewriter.replaceURLs(input, null)
    }


    @Test
    void vml_urls_are_not_replaced() {
        def input = '''
span {
  behavior: url(#default#VML);
}
'''

        def rewriter = new CSSURLRewriter(null, null, null, null)

        assertNull rewriter.replaceURLs(input, null)
    }

    @Test
    void absolute_urls_passed_through_unchanged() {

        def input = '''
body {
  background: white url("/images/back.png") attach-x;
}

div.busy {
  background-image: url( "images/ajax.gif" );
}
'''

        def assetSource = newMock AssetSource
        def resource = newMock Resource
        def asset = newMock Asset

        expect(
            assetSource.getAsset(resource, "images/ajax.gif", null)
        ).andReturn asset

        expect(asset.toClientURL()).andReturn "/ctx/images/ajax.gif"

        replay()

        def rewriter = new CSSURLRewriter(null, null, assetSource, null)

        def output = rewriter.replaceURLs input, resource

        assertEquals output, '''
body {
  background: white url("/images/back.png") attach-x;
}

div.busy {
  background-image: url("/ctx/images/ajax.gif");
}
'''

    }

}