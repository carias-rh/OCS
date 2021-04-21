package eu.europa.ec.eci.oct.web.security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

//the @provider annotation is removed
@Provider
public class CORSResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        MultivaluedMap<String, Object> headers = response.getHeaders();

/*
        //OK - when no auth is required
*/
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
        headers.add("Access-Control-Expose-Headers", "x-ocs-token");
        headers.add("Access-Control-Allow-Headers", "x-ocs-token" + ", *");
        headers.add("Access-Control-Allow-Headers", "Authorization");
        headers.add("Access-Control-Allow-Headers", "x-ocs-token" + ", *");
//        headers.add("Access-Control-Allow-Headers","Cache-Control, Pragma, Expires");

//        boolean hasOrigin = false;
//        MultivaluedMap<String, String> requestHeaders = request.getHeaders();
//        Set<String> keys = requestHeaders.keySet();
//        for(String k : keys){
//            if(k.equals("Origin")){
//                for(String o : requestHeaders.get(k)){
//                    //if needed write a condition here for a trusted lists, for the moment is open to everybody
//                    headers.add("Access-Control-Allow-Origin", o);
//                    hasOrigin = true;
//                }
//            }
//        }
//        if(!hasOrigin){
//            headers.add("Access-Control-Allow-Origin", "*");
//        }
//        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
//        headers.add("Access-Control-Allow-Headers", "Content-Type");
//        headers.add("Access-Control-Allow-Headers", "Authorization");
//        headers.add("Access-Control-Expose-Headers", "x-ocs-token");
//        headers.add("Access-Control-Allow-Headers", "x-ocs-token" + ", *");
//        headers.add("Access-Control-Allow-Credentials", "true");
    }
}
