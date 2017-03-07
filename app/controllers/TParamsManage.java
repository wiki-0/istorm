package controllers;

import java.util.Date;
import java.util.List;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import controllers.CRUD.ObjectType;
import models.TMenu;
import models.TParams;
import models.TUser;

@CRUD.For(TParams.class)
public class TParamsManage extends CRUD
{
	public static void index() 
	{
		String userId = session.get("username");
		TUser user = TUser.find("select t from TUser t where t.T_USER_NAME=?", userId).first();
		String userName ="";
		if(user!=null)
		{
			userName =user.T_USER_NAME;
		}
		if(userId==null )
		{
			String username = params.get("username");
			if(username!=null && !"".equals(username))
			{
				session.put("username", username);
			}
		}
		List<TMenu> tMuneList = TMenu.find("select t from TMenu t where t.T_MENU_TYPE='set' ").fetch();
		String methodName = "tParamsManage";
		render("TParamsManage/index.html",tMuneList, methodName);
	}
	
	public static void save()
	{
		String T_DRM_PARAMS_DESC = params.get("T_DRM_PARAMS_DESC");
		String T_DRM_PARAMS_NAME = params.get("T_DRM_PARAMS_NAME");
		String T_DRM_PARAMS_VALUE = params.get("T_DRM_PARAMS_VALUE");
		String T_DRM_PARAMS_ENABLE = params.get("T_DRM_PARAMS_ENABLE");
		TParams tDrmParams = new TParams();
		tDrmParams.T_DRM_PARAMS_DESC = T_DRM_PARAMS_DESC;
		tDrmParams.T_DRM_PARAMS_NAME = T_DRM_PARAMS_NAME;
		tDrmParams.T_DRM_PARAMS_VALUE = T_DRM_PARAMS_VALUE;
		tDrmParams.T_DRM_PARAMS_ENABLE = T_DRM_PARAMS_ENABLE;
		tDrmParams.save();
		renderText("ok");
	}
	
	public static void update(String id) throws Exception 
	{
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        String T_DRM_PARAMS_ENABLE = params.get("T_DRM_PARAMS_ENABLE");
        TParams tDrmParams = (TParams)object;
        tDrmParams.T_DRM_PARAMS_ENABLE =T_DRM_PARAMS_ENABLE; 
//        object._save();
        tDrmParams.save();
        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }	
	
	@Before
	public static void addType() throws Exception 
	{
		ObjectType type = ObjectType.get(getControllerClass());
		renderArgs.put("type", type);
		String paramName = params.get("paramName");
		System.out.println(paramName);
		String paramEnable = params.get("paramEnable");
		if(paramName!=null && !"".equals(paramName) && paramEnable!=null && !"".equals(paramEnable))
	    {
			request.args.put("where", "  T_DRM_PARAMS_NAME like '%" + paramName + "%' and T_DRM_PARAMS_ENABLE= '1' "  );
	    }
		else if(paramName!=null && !"".equals(paramName) )
	    {
			request.args.put("where", "  T_DRM_PARAMS_NAME like '%" + paramName + "%'  "  );
	    }
		else if( paramEnable!=null && !"".equals(paramEnable))
	    {
			request.args.put("where", "  T_DRM_PARAMS_ENABLE='"+paramEnable+"' "  );
	    }
		
	}
	
	
}
