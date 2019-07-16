package com.young.cas.clientone.config;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;

/**
 * @author Tornado Young
 * @version 2019/7/16 8:30
 */
public class CasClient extends org.pac4j.cas.client.CasClient {
    public CasClient(){
        super();
    }
    public CasClient(CasConfiguration configuration){
        super(configuration);
    }

    @Override
    public RedirectAction getRedirectAction(WebContext context) {
        this.init();
        if (getAjaxRequestResolver().isAjax(context)){
            this.logger.info("AJAX request detected -> returning the appropriate action");
            RedirectAction action=getRedirectActionBuilder().redirect(context);
            this.cleanRequestedUrl(context);
            return getAjaxRequestResolver().buildAjaxResponse(action.getLocation(),context);
        }else {
            final String attemptAuth=(String)context.getSessionStore().get(context,this.getName()+ATTEMPTED_AUTHENTICATION_SUFFIX);
            if (CommonHelper.isNotBlank(attemptAuth)){
                this.cleanAttemptedAuthentication(context);
                this.cleanRequestedUrl(context);
                //401
                //throw HttpAction.unauthorized(context);
                return this.getRedirectActionBuilder().redirect(context);
            }else {
                return this.getRedirectActionBuilder().redirect(context);
            }
        }
    }

    private void cleanRequestedUrl(WebContext context){
        SessionStore<WebContext> sessionStore=context.getSessionStore();
        if (sessionStore.get(context, Pac4jConstants.REQUESTED_URL)!=null){
            sessionStore.set(context,Pac4jConstants.REQUESTED_URL,"");
        }
    }

    private void cleanAttemptedAuthentication(WebContext context){
        SessionStore<WebContext> sessionStore=context.getSessionStore();
        if (sessionStore.get(context,this.getName()+ATTEMPTED_AUTHENTICATION_SUFFIX)!=null){
            sessionStore.set(context,this.getName()+ATTEMPTED_AUTHENTICATION_SUFFIX,"");
        }
    }
}
