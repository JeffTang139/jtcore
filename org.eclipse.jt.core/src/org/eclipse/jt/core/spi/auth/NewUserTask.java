package org.eclipse.jt.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.type.GUID;


/**
 * 新建用户任务
 * 
 * <pre>
 * 使用示例：
 * task = new NewUserTask(userID, userName);
 * task.title = &quot;user title&quot;;
 * task.state = ActorState.DISABLE;
 * task.description = &quot;description string&quot;
 * task.password = &quot;password&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.NewActorTask
 * @author Jeff Tang 2009-11
 */
public final class NewUserTask extends NewActorTask {

	/**
	 * 用户密码，为空时默认为空字符串密码
	 */
	public String password;

	public boolean passwordNeedEncrypt;

	/**
	 * 优先级索引号，未指定时默认为0<br>
	 * 当用户继承了多个角色的时候，为解决用户和各角色之间可能存在的授权冲突问题，需要指定 用户和角色在权限验证时的优先级顺序，该值用户的优先级索引号。
	 */
	public int priorityIndex;

	/**
	 * 为用户分配的角色ID列表<br>
	 * 越先加入的优先级越高。
	 */
	public final List<GUID> assignRoleIDList = new ArrayList<GUID>();

	/**
	 * 创建新建用户任务
	 * 
	 * @param id
	 *            用户ID，不能为空
	 * @param name
	 *            用户名称，不能为空
	 */
	public NewUserTask(GUID id, String name) {
		super(id, name);
		this.passwordNeedEncrypt = true;
	}

}
