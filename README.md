# Personnel-Information-Management-System-Backend

Backend project for the YNU Programming Skills Enhancement Course Project.

任务：

- 在 Account 的业务上，完成超级管理员的查询业务，按照组织分类展示全部组织和成员；根据组织、人员姓名、职位查询人员信息（组合，6个）；查询单个组织的全部人员信息（VO还有个组织总人数字段）；查询单个组织全部推文信息（时间从早到晚排列）

- 在 Organization 业务上，给超级管理员分别展示组织 state 分别为0（待审核），1（通过，就是这个组织现在有哪些人），2（拒绝加入），3（开除的）的组织，记得返回id。给管理员分别展示四个state的人员。还有一个根据组织id查询组织信息的方法，我在这个Controller写了todo，你完善一下就行。

- 在 Tweet 业务上，完成推文的增删，在这个组织的都可以发推文，增加的时候记得判断这个人在这个组织里面，推文图片配置一下OSS服务器。只有本人/管理员可以删除。
