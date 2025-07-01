# <font style="color:rgb(27, 28, 29);">面向下一代教育的智能教学实训智能体软件需求分析</font>
## <font style="color:rgb(27, 28, 29);">第1部分：引言</font>
### <font style="color:rgb(27, 28, 29);">1.1. 项目愿景：构建下一代教育的智能代理</font>
<font style="color:rgb(27, 28, 29);">本项目旨在开发一款具有变革性的“教学实训智能体软件”，该软件将充分利用尖端的开源大型语言模型（LLM）和本地知识库技术。此愿景直接响应了“国家大力推进教育数字化战略”的国家级战略需求，致力于弥合人工智能理论发展与教育领域规模化实践应用之间的鸿沟。该软件将赋能教育者，实现个性化学习，从而培养高素质的应用型人才。项目的成功不仅在于竞赛本身，更在于其设计理念中蕴含的对可扩展性和现实世界适用性的考量，这使得该系统具备了在国家教育数字化转型中扮演示范角色的潜力，尤其在满足“实用价值”和“教育实用与创新性”的评价标准方面具有显著优势。</font>

### <font style="color:rgb(27, 28, 29);">1.2. 本文档的目的与范围</font>
<font style="color:rgb(27, 28, 29);">本文档作为详细的软件需求规格说明书（SRS），旨在全面阐述所提议系统的各项功能、非功能需求以及创新特性。其核心目的在于指导后续的设计、开发与测试阶段，并清晰地阐明系统的高级功能以供评估。文档范围覆盖了面向教师的备课与评估工具、面向学生的交互式学习与练习工具，以及面向管理员的系统管理工具。</font>

### <font style="color:rgb(27, 28, 29);">1.3. 定义、首字母缩写词和缩略语</font>
<font style="color:rgb(27, 28, 29);">为确保本文档的清晰度和一致性，特提供以下关键术语的定义：</font>

+ <font style="color:rgb(27, 28, 29);">LLM: 大型语言模型 (Large Language Model)</font>
+ <font style="color:rgb(27, 28, 29);">RAG: 检索增强生成 (Retrieval Augmented Generation)</font>
+ <font style="color:rgb(27, 28, 29);">KG: 知识图谱 (Knowledge Graph)</font>
+ <font style="color:rgb(27, 28, 29);">ZPD: 最近发展区 (Zone of Proximal Development)</font>
+ <font style="color:rgb(27, 28, 29);">MAS: 多智能体系统 (Multi-Agent System)</font>
+ <font style="color:rgb(27, 28, 29);">HITL: 人在回路 (Human-in-the-Loop)</font>
+ <font style="color:rgb(27, 28, 29);">CoT: 思维链 (Chain-of-Thought)</font>
+ <font style="color:rgb(27, 28, 29);">ToT: 思维树 (Tree-of-Thoughts)</font>
+ <font style="color:rgb(27, 28, 29);">vNMF: 冯·诺依曼多智能体系统框架 (von Neumann Multi-Agent System Framework)</font>
+ <font style="color:rgb(27, 28, 29);">DSA: 动态技能适应 (Dynamic Skill Adaptation)</font>
+ <font style="color:rgb(27, 28, 29);">CFT: 上下文微调 (Contextual Fine-Tuning)</font>
+ <font style="color:rgb(27, 28, 29);">RL: 强化学习 (Reinforcement Learning)</font>
+ <font style="color:rgb(27, 28, 29);">SRS: 软件需求规格说明书 (Software Requirements Specification)</font>
+ <font style="color:rgb(27, 28, 29);">NLP: 自然语言处理 (Natural Language Processing)</font>
+ <font style="color:rgb(27, 28, 29);">UI: 用户界面 (User Interface)</font>
+ <font style="color:rgb(27, 28, 29);">UX: 用户体验 (User Experience)</font>
+ <font style="color:rgb(27, 28, 29);">API: 应用程序编程接口 (Application Programming Interface)</font>

## <font style="color:rgb(27, 28, 29);">第2部分：系统总体描述</font>
### <font style="color:rgb(27, 28, 29);">2.1. 产品视角：模块化、AI驱动的教育生态系统</font>
<font style="color:rgb(27, 28, 29);">本软件并非现有平台的简单附加组件，而是一个独立的、旨在解决传统实训教学中若干核心痛点的教育生态系统。这些痛点包括教师手动备课负担重、题目批改效率低下以及学生缺乏个性化指导等问题。本系统通过将人工智能深度融合到所有核心教育工作流程中——从内容生成到个性化学生互动，再到数据驱动的教学洞察——从而实现差异化。</font>

### <font style="color:rgb(27, 28, 29);">2.2. 高层系统架构（引入多智能体范式）</font>
<font style="color:rgb(27, 28, 29);">系统将采用模块化设计，初步引入多智能体系统（MAS）作为基础架构选择，以期增强系统的专业化、可扩展性和可维护性，具体细节将在第4部分详述。此架构将包含分别服务于教师、学生和管理员的独立模块，所有模块均与一个中央AI核心引擎交互。尽早引入MAS概念，为后续的创新设计奠定了基础，表明该系统不仅仅是一个功能集合，其本身的设计就具有内在的创新性，这也有助于将后续的功能需求在逻辑上组织为由专门的智能体来处理。</font>

### <font style="color:rgb(27, 28, 29);">2.3. 主要用户角色与特征</font>
+ **<font style="color:rgb(27, 28, 29);">教师：</font>**<font style="color:rgb(27, 28, 29);"> 作为学科专家，教师需要工具来减轻在课程规划、资源搜集、题目设计和批改方面的重复性劳动。他们期望获得关于学生学习模式的洞察，以便优化教学策略。</font>
+ **<font style="color:rgb(27, 28, 29);">学生：</font>**<font style="color:rgb(27, 28, 29);"> 作为学习者，学生寻求个性化的、按需的练习、指导和反馈。他们需要一个能够适应其学习节奏、帮助纠正错误理解的互动环境。</font>
+ **<font style="color:rgb(27, 28, 29);">管理员：</font>**<font style="color:rgb(27, 28, 29);"> 负责用户管理、系统维护、知识库更新以及监控系统整体性能的人员。</font>

### <font style="color:rgb(27, 28, 29);">2.4. 核心假设与依赖</font>
+ **<font style="color:rgb(27, 28, 29);">假设：</font>**<font style="color:rgb(27, 28, 29);"> 可获取合适的开源大型语言模型（LLM），最好是中文友好型模型（如赛题要求所述“推荐用国产的模型或对中文友好的模型”），并且这些模型能够进行本地部署或通过API安全访问。</font>
+ **<font style="color:rgb(27, 28, 29);">假设：</font>**<font style="color:rgb(27, 28, 29);"> 本地知识库（例如赛题提供的“《嵌入式Linux开发实践教程》课件资料”）将作为领域特定内容生成和问答的主要信息来源，其大小限制在100MB以内。</font>
+ **<font style="color:rgb(27, 28, 29);">依赖：</font>**<font style="color:rgb(27, 28, 29);"> 开发环境具有灵活性（“硬件、开发环境、开发语言等均不限制”）。</font>

<font style="color:rgb(27, 28, 29);">100MB的知识库大小限制是一个关键约束，它将深刻影响LLM微调策略的选择以及检索增强生成（RAG）的有效性。这一限制要求系统必须具备高效的索引和检索机制。由于知识库容量相对较小，难以覆盖全面的领域知识，系统不能仅仅依赖海量数据来驱动LLM。因此，RAG等技术变得至关重要，但其有效性又与这100MB知识库的质量和结构紧密相关。此外，微调策略必须高效且有针对性，以充分利用这个有限的数据集。这一约束实际上推动了在如何利用本地知识方面的创新，可能涉及从这些数据构建知识图谱（KG）</font><sup>**<font style="color:rgb(87, 91, 95);">1</font>**</sup><font style="color:rgb(27, 28, 29);">，以增强超越简单语义搜索的结构化检索能力。</font>

## <font style="color:rgb(27, 28, 29);">第3部分：具体系统需求</font>
### <font style="color:rgb(27, 28, 29);">3.1. 功能需求</font>
#### <font style="color:rgb(27, 28, 29);">3.1.1. 教师中心模块</font>
+ **<font style="color:rgb(27, 28, 29);">智能备课与设计：</font>**
    - <font style="color:rgb(27, 28, 29);">基于教师上传的本地课程大纲和知识库文档，自动生成教学内容，包括知识点讲解、实训练习与指导、时间分配等。这与LLM在自动化课程创建方面的能力相符</font><sup>**<font style="color:rgb(87, 91, 95);">4</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - <font style="color:rgb(27, 28, 29);">系统应允许教师审查、编辑和定制AI生成的教案，集成人在回路（HITL）原则，以保证内容质量和教学对齐性</font><sup>**<font style="color:rgb(87, 91, 95);">5</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">考核内容生成：</font>**
    - <font style="color:rgb(27, 28, 29);">根据教学内容自动生成多样化的考核题目及参考答案，例如选择题、简答题，以及针对计算机类学科的编程题和配套答案。这借鉴了LLM在代码生成方面的能力</font><sup>**<font style="color:rgb(87, 91, 95);">7</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - <font style="color:rgb(27, 28, 29);">教师应能指定考核参数，如难度、主题覆盖范围、题目类型等。</font>
+ **<font style="color:rgb(27, 28, 29);">学情数据分析与反馈机制：</font>**
    - <font style="color:rgb(27, 28, 29);">自动检测学生提交的答案中的错误，提供错误定位和建设性的修正建议。</font>
    - <font style="color:rgb(27, 28, 29);">分析学生整体数据，总结知识点掌握情况，识别常见误区，并向教师提供教学建议。</font>
    - <font style="color:rgb(27, 28, 29);">将学情数据可视化呈现，便于教师直观解读。</font>

#### <font style="color:rgb(27, 28, 29);">3.1.2. 学生中心模块</font>
+ **<font style="color:rgb(27, 28, 29);">在线学习助手：</font>**
    - <font style="color:rgb(27, 28, 29);">提供实时的问答功能，基于课程内容和本地知识库解答学生的疑问。这需要强大的自然语言处理和信息检索能力。</font>
    - <font style="color:rgb(27, 28, 29);">助手应能提供循序渐进的解释，扮演“耐心辅导员”的角色</font><sup>**<font style="color:rgb(87, 91, 95);">8</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">实时练习与评测助手：</font>**
    - <font style="color:rgb(27, 28, 29);">根据学生的历史练习情况和具体的练习要求，生成个性化的练习题目。</font>
    - <font style="color:rgb(27, 28, 29);">对学生的练习尝试提供即时反馈，包括错误纠正和解释。这符合AI辅导系统提供逐步指导的理念</font><sup>**<font style="color:rgb(87, 91, 95);">9</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

#### <font style="color:rgb(27, 28, 29);">3.1.3. 管理员中心模块</font>
+ **<font style="color:rgb(27, 28, 29);">用户与权限管理：</font>**
    - <font style="color:rgb(27, 28, 29);">安全管理用户账户（管理员、教师、学生），并实施恰当的基于角色的访问控制。</font>
+ **<font style="color:rgb(27, 28, 29);">课件与知识库管理：</font>**
    - <font style="color:rgb(27, 28, 29);">提供界面供管理员上传、组织和管理本地知识库资料（如课程大纲、专业文献）。</font>
    - <font style="color:rgb(27, 28, 29);">管理教师生成或完善的课件资源（教案、练习题），并提供导出功能。</font>
+ **<font style="color:rgb(27, 28, 29);">系统监控与分析仪表盘 (大屏概览)：</font>**
    - <font style="color:rgb(27, 28, 29);">展示关键系统使用统计数据： </font>
        * <font style="color:rgb(27, 28, 29);">教师使用次数统计及活跃板块（当日/本周）。</font>
        * <font style="color:rgb(27, 28, 29);">学生使用次数统计及活跃板块（当日/本周）。</font>
    - <font style="color:rgb(27, 28, 29);">计算并展示“教学效率指数”，考量因素包括： </font>
        * <font style="color:rgb(27, 28, 29);">备课与修正的耗时。</font>
        * <font style="color:rgb(27, 28, 29);">课后练习设计与修正的耗时。</font>
        * <font style="color:rgb(27, 28, 29);">课程优化方向识别（例如，某学科通过率持续偏低）。</font>
    - <font style="color:rgb(27, 28, 29);">展示“学生学习效果”，包括： </font>
        * <font style="color:rgb(27, 28, 29);">平均正确率趋势。</font>
        * <font style="color:rgb(27, 28, 29);">整体知识点掌握情况。</font>
        * <font style="color:rgb(27, 28, 29);">高频错误知识点。</font>

<font style="color:rgb(27, 28, 29);">“教学效率指数”和“学生学习效果”等仪表盘元素不仅是报告工具，更可以作为系统自适应能力以及教师优化教学策略的反馈回路。这些数据同样能够为第4.4.3节中讨论的基于强化学习的策略优化提供信息。仪表盘所要求的“教学效率指数”和“学生学习效果”能够提供关于系统使用和学习成果的量化与质化数据，这些数据具有极高价值。对教师而言，它提供了直接的教学洞察。对AI系统本身，这些数据可用于：1. 验证AI有效性：AI生成的教案是否带来了更好的学生学习成果或更快的教师备课速度？2. 识别AI改进区域：若学生在AI针对某一主题生成的解释上持续遇到困难，则该部分AI的生成策略需要优化。3. 驱动强化学习算法：相关指标（如正确率提升、教师备课时间缩短）可作为强化学习智能体（负责优化内容生成或教学策略）的奖励信号</font><sup>**<font style="color:rgb(87, 91, 95);">10</font>**</sup><font style="color:rgb(27, 28, 29);">，从而构建一个自我完善的生态系统。</font>

### <font style="color:rgb(27, 28, 29);">3.2. AI核心引擎需求</font>
#### <font style="color:rgb(27, 28, 29);">3.2.1. LLM集成与管理：</font>
+ <font style="color:rgb(27, 28, 29);">系统需至少集成一个开源大型语言模型作为核心技术组件。优先考虑对中文支持良好且能够本地部署或通过安全API访问的模型。</font>
+ <font style="color:rgb(27, 28, 29);">高效利用本地知识库（最大100MB）作为LLM响应和内容生成的主要依据。这要求强大的检索增强生成（RAG）技术。</font>
+ <font style="color:rgb(27, 28, 29);">建立机制，以便在新的、更优的开源模型出现时，能够方便地更新或切换LLM。</font>

#### <font style="color:rgb(27, 28, 29);">3.2.2. 自然语言处理（NLP）能力：</font>
+ <font style="color:rgb(27, 28, 29);">具备高级NLP能力，以理解教师用于内容生成的提示、学生的查询以及分析学生提交的文本答案。</font>
+ <font style="color:rgb(27, 28, 29);">在对话中实现语义理解、意图识别和上下文管理。</font>

#### <font style="color:rgb(27, 28, 29);">3.2.3. 自动化评估技术：</font>
+ <font style="color:rgb(27, 28, 29);">具备评估学生答案的算法，包括文本回复和代码。</font>
+ <font style="color:rgb(27, 28, 29);">对于代码评估，应包括功能正确性，并可能涉及代码风格和效率的评估。</font>
+ <font style="color:rgb(27, 28, 29);">提供错误定位和建设性反馈的机制。</font>

### <font style="color:rgb(27, 28, 29);">3.3. 数据管理与持久化</font>
+ <font style="color:rgb(27, 28, 29);">安全存储用户数据、课程资料、学生提交内容、学习表现分析数据以及系统日志。</font>
+ <font style="color:rgb(27, 28, 29);">数据库设计应支持高效查询，以服务于分析功能和个性化内容分发。</font>
+ <font style="color:rgb(27, 28, 29);">遵守与教育数据相关的隐私法规。</font>

### <font style="color:rgb(27, 28, 29);">3.4. 用户界面（UI）与用户体验（UX）考量</font>
+ <font style="color:rgb(27, 28, 29);">为教师、学生和管理员设计直观易用的、符合其特定需求的界面。</font>
+ <font style="color:rgb(27, 28, 29);">清晰呈现AI生成的内容、分析数据和反馈信息。</font>
+ <font style="color:rgb(27, 28, 29);">采用响应式设计，以确保在各种设备上的可访问性（虽未明确要求，但属良好实践）。</font>

### <font style="color:rgb(27, 28, 29);">3.5. 非功能性需求</font>
+ **<font style="color:rgb(27, 28, 29);">生成内容的准确性与关联性：</font>**
    - <font style="color:rgb(27, 28, 29);">确保生成的内容（知识讲解、练习题、答案）与本地知识库高度相关且准确。这是关键的评分标准（“生成的内容、练习与答案与本地知识库的关联性和准确性”）。</font>
    - <font style="color:rgb(27, 28, 29);">建立机制以验证LLM输出的事实准确性，可能包括教师反馈回路和在知识库内的交叉引用。</font>
+ **<font style="color:rgb(27, 28, 29);">性能与可扩展性：</font>**
    - <font style="color:rgb(27, 28, 29);">为学生互动问答和实时练习反馈提供快速响应时间。</font>
    - <font style="color:rgb(27, 28, 29);">高效处理教师的内容生成和数据分析请求。</font>
    - <font style="color:rgb(27, 28, 29);">系统设计应能处理典型教育场景下合理的并发用户量（虽未提供具体数字，但可扩展性应作为设计考量，尤其是在采用MAS架构时</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">）。</font>
+ **<font style="color:rgb(27, 28, 29);">易用性与可访问性：</font>**
    - <font style="color:rgb(27, 28, 29);">为所有用户角色提供易学易用的界面。</font>
    - <font style="color:rgb(27, 28, 29);">考虑无障碍标准，确保不同需求的用户均可使用（通用最佳实践）。</font>
+ **<font style="color:rgb(27, 28, 29);">可靠性与可用性：</font>**
    - <font style="color:rgb(27, 28, 29);">保证系统高正常运行时间和鲁棒性，防止教学活动中断。</font>
    - <font style="color:rgb(27, 28, 29);">具备错误处理和恢复机制。</font>
+ **<font style="color:rgb(27, 28, 29);">安全性与数据隐私：</font>**
    - <font style="color:rgb(27, 28, 29);">保护敏感的学生数据和课程数据免遭未经授权的访问。</font>
    - <font style="color:rgb(27, 28, 29);">确保数据的安全存储和传输。</font>
    - <font style="color:rgb(27, 28, 29);">遵循教育数据处理的伦理准则</font><sup>**<font style="color:rgb(87, 91, 95);">13</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">可维护性与可扩展性：</font>**
    - <font style="color:rgb(27, 28, 29);">采用模块化设计，便于更新、修复缺陷以及添加新功能或新的LLM。MAS方法对此提供了支持</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

## <font style="color:rgb(27, 28, 29);">第4部分：高级功能与创新路线图</font>
_<font style="color:rgb(27, 28, 29);">本部分详细阐述了旨在显著提升系统能力、教育影响和竞争地位的创新方法。这些方法与用户对前沿AI技术的兴趣以及竞赛对“技术与创新性”和“教育实用与创新性”的侧重直接相关。</font>_

### <font style="color:rgb(27, 28, 29);">4.1. 精细化的多智能体系统架构</font>
#### <font style="color:rgb(27, 28, 29);">4.1.1. 采用精细化多智能体系统（MAS）的理由：</font>
+ <font style="color:rgb(27, 28, 29);">从单体架构转向MAS，涉及将系统分解为更小、专门化且独立运作的智能体</font><sup>**<font style="color:rgb(87, 91, 95);">15</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">益处：</font>**
    - **<font style="color:rgb(27, 28, 29);">专业化：</font>**<font style="color:rgb(27, 28, 29);"> 每个智能体可以针对特定任务（如课程生成、评估、辅导）进行微调或设计，从而提升整体性能和准确性</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">。例如，“课程智能体”可以专注于教学设计原则，而“评估智能体”则聚焦于心理测量学和题目生成。</font>
    - **<font style="color:rgb(27, 28, 29);">可扩展性：</font>**<font style="color:rgb(27, 28, 29);"> 任务可以被分发，通过增加更多智能体实例来横向扩展系统以应对增长的负载</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - **<font style="color:rgb(27, 28, 29);">模块化与可维护性：</font>**<font style="color:rgb(27, 28, 29);"> 简化了独立组件的开发、测试和更新，而不影响整个系统</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - **<font style="color:rgb(27, 28, 29);">灵活性：</font>**<font style="color:rgb(27, 28, 29);"> 智能体可以在不同工作流程中复用，或组合成新的系统</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">挑战：</font>**<font style="color:rgb(27, 28, 29);"> 需要强大的协调机制、智能体间通信协议、状态管理，以及可能需要一个监督智能体来编排复杂任务</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">。AWS Bedrock和Azure AI Foundry等平台提供了管理此类系统的框架示例</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

#### <font style="color:rgb(27, 28, 29);">4.1.2. 提议的智能体角色与交互（受vNMF和教育需求启发）：</font>
<font style="color:rgb(27, 28, 29);">借鉴冯·诺依曼多智能体系统框架（vNMF）将智能体分解为控制、逻辑、存储和输入/输出单元的理念</font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">，可以定义以下专门化的教育智能体：</font>

+ **<font style="color:rgb(27, 28, 29);">课程智能体 (Curriculum Agent):</font>**<font style="color:rgb(27, 28, 29);"> 负责解读教学大纲、生成教案、构建教学内容结构、识别先修知识。利用任务分解（如思维链CoT、思维树ToT）进行复杂的课程设计</font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">评估智能体 (Assessment Agent):</font>**<font style="color:rgb(27, 28, 29);"> 生成多样化的评估项目（包括编程练习</font><sup>**<font style="color:rgb(87, 91, 95);">7</font>**</sup><font style="color:rgb(27, 28, 29);">），确保多样性、难度校准，并生成参考答案。可使用LLM+Planner进行复杂评估序列的规划</font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">辅导智能体 (Tutoring Agent, 面向学生):</font>**<font style="color:rgb(27, 28, 29);"> 管理学生互动，回答问题，提供实时练习，进行解释说明，并给予个性化反馈。采用动态教学角色（见4.2.3节）。</font>
+ **<font style="color:rgb(27, 28, 29);">学生建模智能体 (Student Modeling Agent):</font>**<font style="color:rgb(27, 28, 29);"> 追踪学生进度，推断知识状态（可能使用最近发展区ZPD概念，见4.3.1节），识别错误概念，并监控学生参与度</font><sup>**<font style="color:rgb(87, 91, 95);">19</font>**</sup><font style="color:rgb(27, 28, 29);">。此智能体的输出将反馈给辅导智能体和课程智能体以进行适应性调整。</font>
+ **<font style="color:rgb(27, 28, 29);">对话管理智能体 (Dialogue Management Agent):</font>**<font style="color:rgb(27, 28, 29);"> 专注于维护连贯的、上下文感知的长期对话，保持角色一致性（见4.2.1节）。</font>
+ **<font style="color:rgb(27, 28, 29);">知识库智能体 (Knowledge Base Agent, RAG与KG专家):</font>**<font style="color:rgb(27, 28, 29);"> 管理与本地知识库的交互，执行高效检索，并可能构建/利用知识图谱（见4.4.4节）为其他智能体提供可靠信息。</font>
+ **<font style="color:rgb(27, 28, 29);">评估与分析智能体 (Evaluation & Analytics Agent):</font>**<font style="color:rgb(27, 28, 29);"> 从学生互动和评估中收集数据，执行自动评分，为教师仪表盘生成分析结果，并为系统自我改进（例如，用于强化学习）提供洞察。</font>
+ **<font style="color:rgb(27, 28, 29);">监督智能体 (Supervisor Agent, 可选但推荐):</font>**<font style="color:rgb(27, 28, 29);"> 编排复杂的多步骤任务，将子任务委派给专门的智能体，并整合输出，类似于AWS Bedrock的多智能体协作功能</font><sup>**<font style="color:rgb(87, 91, 95);">15</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

**<font style="color:rgb(27, 28, 29);">交互机制：</font>**<font style="color:rgb(27, 28, 29);"> 智能体之间将通过定义良好的API或消息总线进行通信，按需共享上下文数据和状态信息。例如，学生建模智能体对学生ZPD的评估将为课程智能体的后续内容推荐以及辅导智能体的支架级别提供依据。vNMF框架</font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">为思考单个智能体的能力（任务分解、自我反思、记忆处理、工具调用）提供了一个结构化的方式，这些能力可以映射到特定的教育角色。例如，辅导智能体将大量使用记忆处理来进行长期对话，并使用自我反思来改进其解释策略。这种从用户对“精细化多智能体架构”的需求出发，结合研究成果</font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">确认其专业化和可扩展性等益处，并将竞赛所要求的教师和学生功能自然映射到专门的智能体角色，再利用vNMF框架</font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">为每个教育智能体（课程、评估、辅导）提供概念模型（包含控制、逻辑、存储、I/O等核心操作，并针对其特定教育功能进行定制，例如课程智能体使用任务分解将大纲分解为课程模块，辅导智能体使用记忆处理回忆过去的互动并通过自我反思改进其解释策略），从而为智能体设计提供了超越简单命名的更深层次、更结构化的方法。</font>

**<font style="color:rgb(27, 28, 29);">表4.1.2：教育多智能体系统：角色、职责与使能技术</font>**

| **<font style="color:rgb(27, 28, 29);">智能体角色</font>** | **<font style="color:rgb(27, 28, 29);">核心职责</font>** | **<font style="color:rgb(27, 28, 29);">关键AI技术/LLM操作 (例如vNMF操作)</font>** | **<font style="color:rgb(27, 28, 29);">相关研究支撑</font>** |
| --- | --- | --- | --- |
| <font style="color:rgb(27, 28, 29);">课程智能体</font> | <font style="color:rgb(27, 28, 29);">解读大纲、生成教案、构建内容结构、识别先修知识</font> | <font style="color:rgb(27, 28, 29);">任务分解 (CoT, ToT), 内容生成, 教学设计原则</font> | <sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">评估智能体</font> | <font style="color:rgb(27, 28, 29);">生成多样化评估（含编程题）、难度校准、生成答案</font> | <font style="color:rgb(27, 28, 29);">问题生成, 代码生成, LLM+Planner, 自动评分</font> | <sup>**<font style="color:rgb(87, 91, 95);">7</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">辅导智能体</font> | <font style="color:rgb(27, 28, 29);">学生互动、问答、实时练习、解释、个性化反馈</font> | <font style="color:rgb(27, 28, 29);">自然语言理解, 对话管理, 动态教学角色, 支架式教学</font> | <sup>**<font style="color:rgb(87, 91, 95);">8</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">学生建模智能体</font> | <font style="color:rgb(27, 28, 29);">追踪进度、推断知识状态 (ZPD)、识别误解、监控参与度</font> | <font style="color:rgb(27, 28, 29);">知识追踪, 参与度分析, 机器学习</font> | <sup>**<font style="color:rgb(87, 91, 95);">19</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">对话管理智能体</font> | <font style="color:rgb(27, 28, 29);">维护长期对话连贯性、上下文感知、角色一致性</font> | <font style="color:rgb(27, 28, 29);">事件记忆, 角色建模, 记忆管理</font> | <sup>**<font style="color:rgb(87, 91, 95);">21</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">知识库智能体</font> | <font style="color:rgb(27, 28, 29);">RAG, KG构建与利用, 为其他智能体提供基于知识库的信息</font> | <font style="color:rgb(27, 28, 29);">RAG, 知识图谱构建与查询, 信息检索</font> | <sup>**<font style="color:rgb(87, 91, 95);">1</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">评估与分析智能体</font> | <font style="color:rgb(27, 28, 29);">收集交互与评估数据、自动评分、生成教师仪表盘分析、为系统自改进提供洞察</font> | <font style="color:rgb(27, 28, 29);">数据分析, 机器学习, 可视化</font> | |
| <font style="color:rgb(27, 28, 29);">监督智能体 (可选)</font> | <font style="color:rgb(27, 28, 29);">编排复杂多步任务、委派子任务、整合输出</font> | <font style="color:rgb(27, 28, 29);">任务调度, 工作流管理</font> | <sup>**<font style="color:rgb(87, 91, 95);">15</font>**</sup> |


### <font style="color:rgb(27, 28, 29);">4.2. 增强的教学互动</font>
#### <font style="color:rgb(27, 28, 29);">4.2.1. 具有连贯角色的长期对话管理：</font>
+ <font style="color:rgb(27, 28, 29);">实施机制以在扩展的多会话对话中保持上下文和一致性</font><sup>**<font style="color:rgb(87, 91, 95);">24</font>**</sup><font style="color:rgb(27, 28, 29);">。这对于建立融洽关系和有效的长期学习关系至关重要。</font>
+ <font style="color:rgb(27, 28, 29);">利用事件记忆（存储历史互动摘要）和角色一致性（维持一致的智能体个性和记住用户特征）技术</font><sup>**<font style="color:rgb(87, 91, 95);">21</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">探索处理过时信息的记忆管理技术，例如</font><sup>**<font style="color:rgb(87, 91, 95);">25</font>**</sup><font style="color:rgb(27, 28, 29);">中描述的PASS、REPLACE、APPEND、DELETE操作，以确保聊天机器人使用最新信息，从而提高参与度和人性化程度。</font>
+ <font style="color:rgb(27, 28, 29);">LD-Agent框架</font><sup>**<font style="color:rgb(87, 91, 95);">26</font>**</sup><font style="color:rgb(27, 28, 29);">提供了一个模型无关的方法，包含事件感知、角色提取和响应生成模块，适用于本系统。</font>

#### <font style="color:rgb(27, 28, 29);">4.2.2. 用于复杂推理的思维链（CoT）和思维树（ToT）：</font>
+ <font style="color:rgb(27, 28, 29);">采用CoT提示引导LLM在为复杂主题生成解释或解决多步骤问题时“逐步思考”</font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">。这可以提高准确性，并使推理过程对学生更清晰。</font>
+ <font style="color:rgb(27, 28, 29);">对于更复杂的任务，可以探索ToT，它允许LLM探索多个推理路径（如树状结构）并自我评估中间想法，从而产生更稳健的解决方案</font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">。这对于评估智能体生成复杂问题解决方案或辅导智能体解释复杂概念时尤其有用。</font>

#### <font style="color:rgb(27, 28, 29);">4.2.3. 动态教学角色实现：</font>
+ <font style="color:rgb(27, 28, 29);">为辅导智能体设计并实现不同的教学角色（例如，苏格拉底式提问者、耐心导师、鼓励型教练）</font><sup>**<font style="color:rgb(87, 91, 95);">21</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">角色应能根据学生的学习风格、当前情绪状态（如果可通过参与度线索检测）以及学习任务的性质进行调整。例如，采用苏格拉底式方法</font><sup>**<font style="color:rgb(87, 91, 95);">29</font>**</sup><font style="color:rgb(27, 28, 29);">促进批判性思维，对基础知识则采用更直接的导师方法。</font>
+ <font style="color:rgb(27, 28, 29);">角色一致性是关键；所选角色应在整个互动过程中保持，除非出于教学原因需要刻意转变</font><sup>**<font style="color:rgb(87, 91, 95);">21</font>**</sup><font style="color:rgb(27, 28, 29);">。LLM智能体的终身学习能力</font><sup>**<font style="color:rgb(87, 91, 95);">31</font>**</sup><font style="color:rgb(27, 28, 29);">有助于基于互动随时间调整和完善这些角色。</font>
+ <font style="color:rgb(27, 28, 29);">系统可以使用对话状态和学生模型输出</font><sup>**<font style="color:rgb(87, 91, 95);">20</font>**</sup><font style="color:rgb(27, 28, 29);">来动态调整角色。例如，如果学生模型显示学生非常沮丧，角色可能会转变为更具鼓励性并提供更简单的支架。</font>

<font style="color:rgb(27, 28, 29);">将长期对话、CoT/ToT和动态角色相结合，可以创造出高度自适应和类似人类的辅导体验，超越简单的问答模式。这直接满足了“教育实用与创新性”的要求。有效的辅导不仅仅是信息传递，它涉及持续的、适应性的互动。长期对话</font><sup>**<font style="color:rgb(87, 91, 95);">24</font>**</sup><font style="color:rgb(27, 28, 29);">确保了随时间的连续性和个性化。CoT/ToT </font><sup>**<font style="color:rgb(87, 91, 95);">17</font>**</sup><font style="color:rgb(27, 28, 29);">使AI能够清晰地阐述复杂推理，这对于解释困难概念或解决问题的步骤至关重要。动态角色</font><sup>**<font style="color:rgb(87, 91, 95);">20</font>**</sup><font style="color:rgb(27, 28, 29);">允许辅导员根据学生的需求调整其互动风格（例如，苏格拉底式用于探索，导师式用于直接指导）。这三个要素的协同作用创造了一个更具吸引力、更有效、更像人类的AI辅导员。例如，一个在长期互动中保持“苏格拉底式导师”角色的辅导智能体，可以利用其事件记忆，使用CoT来解释为什么某个特定的苏格拉底式问题与学生之前的错误相关。这与无状态的、通用的响应相比，是一个显著的进步。</font>

### <font style="color:rgb(27, 28, 29);">4.3. 个性化与自适应学习生态系统</font>
#### <font style="color:rgb(27, 28, 29);">4.3.1. 基于技能图谱和ZPD的学生建模实现动态学习路径生成：</font>
+ <font style="color:rgb(27, 28, 29);">开发自适应学习路径，根据个体学生的特定需求定制内容序列</font><sup>**<font style="color:rgb(87, 91, 95);">4</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">通过将复杂技能分解为子技能，并根据它们之间的依赖关系进行排列，构建一个</font>**<font style="color:rgb(27, 28, 29);">技能图谱</font>**<font style="color:rgb(27, 28, 29);">，这类似于动态技能适应（DSA）框架</font><sup>**<font style="color:rgb(87, 91, 95);">34</font>**</sup><font style="color:rgb(27, 28, 29);">。该图谱将映射本地知识库（例如，Linux教程）中的概念。</font>
+ <font style="color:rgb(27, 28, 29);">评估学生的最近发展区（ZPD）——即学习者独立完成任务的能力与在指导下能够完成任务的能力之间的差距</font><sup>**<font style="color:rgb(87, 91, 95);">22</font>**</sup><font style="color:rgb(27, 28, 29);">。这可以通过互动模式、错误类型以及对支架的反应来推断。</font>
+ <font style="color:rgb(27, 28, 29);">系统应优化练习和内容的序列，以在学生的ZPD内提供最佳挑战，从而最大化学习进展，可能使用诸如ZPDES之类的算法</font><sup>**<font style="color:rgb(87, 91, 95);">37</font>**</sup><font style="color:rgb(27, 28, 29);">或调整DSA原则</font><sup>**<font style="color:rgb(87, 91, 95);">34</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">LLM的互动（查询、错误、响应）可以为推断ZPD提供丰富的数据</font><sup>**<font style="color:rgb(87, 91, 95);">22</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

#### <font style="color:rgb(27, 28, 29);">4.3.2. 主动与情境化支架策略：</font>
+ <font style="color:rgb(27, 28, 29);">实施AI驱动的支架教学，提供临时的、量身定制的支持，并随着学习者能力的增强而逐渐减少这种支持</font><sup>**<font style="color:rgb(87, 91, 95);">38</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">支架应具有</font>**<font style="color:rgb(27, 28, 29);">情境性</font>**<font style="color:rgb(27, 28, 29);">，即根据对学习者当前能力和任务特征的动态评估来提供</font><sup>**<font style="color:rgb(87, 91, 95);">39</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">基于预测性分析（例如，标记有风险的学生或参与度下降的学生）采用主动干预措施</font><sup>**<font style="color:rgb(87, 91, 95);">9</font>**</sup><font style="color:rgb(27, 28, 29);">。</font><sup>**<font style="color:rgb(87, 91, 95);">19</font>**</sup><font style="color:rgb(27, 28, 29);">中描述的参与度预测模型（EPM），通过分析犹豫时间和提示请求来进行预测，是一个相关的例子。</font>
+ <font style="color:rgb(27, 28, 29);">提供多种类型的支架：概念性支架（解释概念）、程序性支架（如何使用工具/功能）、策略性支架（建议方法）和元认知支架（促进反思）</font><sup>**<font style="color:rgb(87, 91, 95);">39</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

#### <font style="color:rgb(27, 28, 29);">4.3.3. 实时参与度追踪与干预：</font>
+ <font style="color:rgb(27, 28, 29);">学生建模智能体应基于互动模式（例如，响应时间、错误率、提示请求频率）整合实时参与度追踪功能</font><sup>**<font style="color:rgb(87, 91, 95);">19</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">如果检测到参与度下降，辅导智能体可以主动干预，例如： </font>
    - <font style="color:rgb(27, 28, 29);">调整难度级别。</font>
    - <font style="color:rgb(27, 28, 29);">改变教学角色（例如，变得更鼓励）。</font>
    - <font style="color:rgb(27, 28, 29);">提供不同类型的解释或活动。</font>
    - <font style="color:rgb(27, 28, 29);">发送激励性消息。</font>

<font style="color:rgb(27, 28, 29);">技能图谱、ZPD建模、动态支架和参与度追踪的整合，创造了一个深度个性化的系统，不仅能实时调整</font>_<font style="color:rgb(27, 28, 29);">教什么</font>_<font style="color:rgb(27, 28, 29);">，还能调整</font>_<font style="color:rgb(27, 28, 29);">怎么教</font>_<font style="color:rgb(27, 28, 29);">。这超越了简单的内容个性化，实现了真正的教学自适应。个性化学习是目标，传统ITS通常使用固定路径或简单分支。技能图谱</font><sup>**<font style="color:rgb(87, 91, 95);">34</font>**</sup><font style="color:rgb(27, 28, 29);">提供了领域知识和依赖关系的结构化表示，对于逻辑排序至关重要。ZPD </font><sup>**<font style="color:rgb(87, 91, 95);">22</font>**</sup><font style="color:rgb(27, 28, 29);">为选择具有挑战性但可实现的任务提供了理论基础，从而最大化学习效果，LLM互动有助于推断ZPD。动态支架</font><sup>**<font style="color:rgb(87, 91, 95);">38</font>**</sup><font style="color:rgb(27, 28, 29);">为在ZPD内学习的学生提供了必要的支持。参与度追踪</font><sup>**<font style="color:rgb(87, 91, 95);">19</font>**</sup><font style="color:rgb(27, 28, 29);">确保学生保持对学习的接受状态，并在他们变得不投入或沮丧时进行干预。综合这些要素：系统使用技能图谱来理解内容依赖关系；使用ZPD建模来选择合适的后续任务；提供动态支架以支持学生完成这些任务；监控参与度以确保学生有效学习，并在需要时进行干预。这创造了一个远比简单自适应内容选择更为复杂的真正自适应学习循环。</font>

### <font style="color:rgb(27, 28, 29);">4.4. 高效、可靠且持续改进的AI</font>
#### <font style="color:rgb(27, 28, 29);">4.4.1. 面向高效特定任务模型的知识蒸馏：</font>
+ <font style="color:rgb(27, 28, 29);">采用知识蒸馏技术，将大型通用（教师）LLM的能力迁移到更小、更高效（学生）的模型中，这些学生模型针对系统内的特定任务（例如，用于问答的学生模型，用于评估评分的另一个学生模型）进行优化</font><sup>**<font style="color:rgb(87, 91, 95);">43</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">这对于在资源受限的环境中获得良好性能至关重要，并且如果使用基于API的LLM，可以降低运营成本。</font>
+ <font style="color:rgb(27, 28, 29);">技术包括使用教师LLM生成合成数据和软标签来训练学生模型</font><sup>**<font style="color:rgb(87, 91, 95);">43</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">专注于特定任务的蒸馏，以便在有针对性的教育功能（如问题生成或自由文本解释生成）上获得更好的性能</font><sup>**<font style="color:rgb(87, 91, 95);">43</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

#### <font style="color:rgb(27, 28, 29);">4.4.2. 基于本地知识的上下文与指令微调：</font>
+ <font style="color:rgb(27, 28, 29);">在提供的本地知识库（“《嵌入式Linux开发实践教程》课件资料”）上微调选定的开源LLM，使其适应特定领域</font><sup>**<font style="color:rgb(87, 91, 95);">50</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">利用</font>**<font style="color:rgb(27, 28, 29);">上下文微调（CFT）</font>**<sup>**<font style="color:rgb(87, 91, 95);">51</font>**</sup><font style="color:rgb(27, 28, 29);">，其中模仿人类认知策略的教学提示指导LLM在领域特定文档（如教科书）上的学习过程。这可以提高对本地知识的解读和理解。 </font>
    - <font style="color:rgb(27, 28, 29);">这包括为训练创建（上下文提示，教科书输出文本）对</font><sup>**<font style="color:rgb(87, 91, 95);">51</font>**</sup><font style="color:rgb(27, 28, 29);">。上下文提示（例如，“批判性地分析接下来的信息。寻找潜在的假设……”）引导模型学习教科书内容中的</font>_<font style="color:rgb(27, 28, 29);">语义</font>_<font style="color:rgb(27, 28, 29);">和</font>_<font style="color:rgb(27, 28, 29);">关系</font>_<font style="color:rgb(27, 28, 29);">，而不仅仅是表面模式。</font>
+ <font style="color:rgb(27, 28, 29);">从教科书中开发</font>**<font style="color:rgb(27, 28, 29);">指令微调数据集</font>**<sup>**<font style="color:rgb(87, 91, 95);">54</font>**</sup><font style="color:rgb(27, 28, 29);">。这涉及生成指令-响应对，其中指令是与教科书内容相关的问题或任务，响应是源自教科书的事实性答案或解释。这有助于LLM学习遵循与教育领域相关的指令。</font>
+ <font style="color:rgb(27, 28, 29);">在对新领域进行微调时，解决灾难性遗忘等挑战，并优化令牌效率</font><sup>**<font style="color:rgb(87, 91, 95);">56</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

**<font style="color:rgb(27, 28, 29);">表4.4.2：教育内容自适应的微调方法比较</font>**

| **<font style="color:rgb(27, 28, 29);">方法</font>** | **<font style="color:rgb(27, 28, 29);">描述</font>** | **<font style="color:rgb(27, 28, 29);">对教育的主要益处</font>** | **<font style="color:rgb(27, 28, 29);">挑战/考量</font>** | **<font style="color:rgb(27, 28, 29);">项目内应用示例</font>** | **<font style="color:rgb(27, 28, 29);">相关研究支撑</font>** |
| --- | --- | --- | --- | --- | --- |
| <font style="color:rgb(27, 28, 29);">标准监督微调 (SFT)</font> | <font style="color:rgb(27, 28, 29);">使用特定任务的标注数据更新预训练模型的权重。</font> | <font style="color:rgb(27, 28, 29);">提高模型在特定教育任务上的表现。</font> | <font style="color:rgb(27, 28, 29);">需要大量高质量标注数据，可能发生过拟合或灾难性遗忘。</font> | <font style="color:rgb(27, 28, 29);">基于教科书内容微调LLM以生成特定主题的练习题。</font> | <sup>**<font style="color:rgb(87, 91, 95);">50</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">上下文微调 (CFT)</font> | <font style="color:rgb(27, 28, 29);">在微调过程中加入模仿人类认知策略的上下文提示，引导模型学习领域知识的语义和关系。</font> | <font style="color:rgb(27, 28, 29);">增强模型对本地知识库（如教科书）的深层理解和推理能力，提高学习效率。</font> | <font style="color:rgb(27, 28, 29);">提示设计是关键，需要精心构建以有效引导学习。</font> | <font style="color:rgb(27, 28, 29);">使用上下文提示微调LLM，使其能从Linux教程中提取并解释复杂概念。</font> | <sup>**<font style="color:rgb(87, 91, 95);">51</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">指令微调 (Instruction Tuning)</font> | <font style="color:rgb(27, 28, 29);">使用大量的（指令，输出）对进行微调，使模型学会遵循各种自然语言指令。</font> | <font style="color:rgb(27, 28, 29);">提高模型遵循教师或学生用自然语言提出的具体教学相关指令的能力。</font> | <font style="color:rgb(27, 28, 29);">需要构建多样化且高质量的指令数据集。</font> | <font style="color:rgb(27, 28, 29);">基于教科书内容创建指令数据集，训练LLM回答关于嵌入式Linux的具体问题。</font> | <sup>**<font style="color:rgb(87, 91, 95);">54</font>**</sup> |


#### <font style="color:rgb(27, 28, 29);">4.4.3. 用于优化辅导策略和对话策略的强化学习（RL）：</font>
+ <font style="color:rgb(27, 28, 29);">应用RL来优化辅导智能体的决策（例如，何时干预，提供何种类型的提示，如何调整解释），基于学生互动和学习成果</font><sup>**<font style="color:rgb(87, 91, 95);">10</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">RL智能体可以学习一个策略，以最大化学生的长期学习和参与度，使用来自学生模型和分析仪表盘的指标作为奖励信号。</font>
+ <font style="color:rgb(27, 28, 29);">RL也可以用于改进对话策略，以实现更自然和有效的对话</font><sup>**<font style="color:rgb(87, 91, 95);">19</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

#### <font style="color:rgb(27, 28, 29);">4.4.4. 用于事实 grounding 和可解释反馈的知识图谱增强RAG：</font>
+ <font style="color:rgb(27, 28, 29);">从本地知识库（例如Linux教科书）构建知识图谱（KG）</font><sup>**<font style="color:rgb(87, 91, 95);">1</font>**</sup><font style="color:rgb(27, 28, 29);">。这涉及提取实体和关系，以创建领域知识的结构化表示。</font>
+ <font style="color:rgb(27, 28, 29);">将此KG与RAG框架集成（KG-RAG）</font><sup>**<font style="color:rgb(87, 91, 95);">27</font>**</sup><font style="color:rgb(27, 28, 29);">。这可以增强检索精度，超越简单的语义搜索，使系统能够理解和利用概念之间的结构化关系。</font>
+ <font style="color:rgb(27, 28, 29);">KG-RAG通过将响应基于事实性的结构化数据，减少幻觉并提高鲁棒性</font><sup>**<font style="color:rgb(87, 91, 95);">27</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">至关重要的是，它增强了</font>**<font style="color:rgb(27, 28, 29);">可解释性</font>**<font style="color:rgb(27, 28, 29);">：从KG中检索到的结构化数据可以作为反馈的一部分呈现给学生，说明知识点的联系以及答案是如何得出的</font><sup>**<font style="color:rgb(87, 91, 95);">27</font>**</sup><font style="color:rgb(27, 28, 29);">。这比黑箱LLM响应更透明。</font>
+ <font style="color:rgb(27, 28, 29);">在KG-RAG框架内利用问题分解进行多跳推理</font><sup>**<font style="color:rgb(87, 91, 95);">27</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

<font style="color:rgb(27, 28, 29);">（特别是CFT和指令微调）与本地知识库的结合、用于可靠检索的KG-RAG以及用于持续策略优化的RL，共同创建了一个强大的人工智能引擎。该引擎不仅对特定领域知识渊博，而且能够随着时间的推移改进其教学策略。系统必须准确并基于本地100MB知识库。微调</font><sup>**<font style="color:rgb(87, 91, 95);">50</font>**</sup><font style="color:rgb(27, 28, 29);">使LLM更熟悉领域的语言和概念。CFT </font><sup>**<font style="color:rgb(87, 91, 95);">51</font>**</sup><font style="color:rgb(27, 28, 29);">特别帮助它学习</font>_<font style="color:rgb(27, 28, 29);">如何</font>_<font style="color:rgb(27, 28, 29);">从这些内容中学习。然而，LLM仍可能产生幻觉。RAG </font><sup>**<font style="color:rgb(87, 91, 95);">28</font>**</sup><font style="color:rgb(27, 28, 29);">将响应基于检索到的文本。KG-RAG </font><sup>**<font style="color:rgb(87, 91, 95);">27</font>**</sup><font style="color:rgb(27, 28, 29);">优于简单的向量RAG，因为KG捕获结构化关系，从而实现更好的推理和可解释性——这对于教育至关重要。KG可以从教科书构建</font><sup>**<font style="color:rgb(87, 91, 95);">1</font>**</sup><font style="color:rgb(27, 28, 29);">。RL </font><sup>**<font style="color:rgb(87, 91, 95);">10</font>**</sup><font style="color:rgb(27, 28, 29);">允许系统从交互数据中学习最佳的教学和对话策略，从而不断提高其有效性。因此，这些技术并非相互排斥，而是协同作用：微调提供了一个良好的基础模型。KG-RAG确保其输出有据可查且可解释。RL优化其交互行为。知识蒸馏</font><sup>**<font style="color:rgb(87, 91, 95);">43</font>**</sup><font style="color:rgb(27, 28, 29);">使该系统的专门组件高效运行。</font>

### <font style="color:rgb(27, 28, 29);">4.5. 高级评估与反馈能力</font>
#### <font style="color:rgb(27, 28, 29);">4.5.1. LLM驱动的多样化与复杂练习生成：</font>
+ <font style="color:rgb(27, 28, 29);">开发提示和技术，使LLM能够从技术手册（如“《嵌入式Linux开发实践教程》”）生成各种类型的练习</font><sup>**<font style="color:rgb(87, 91, 95);">67</font>**</sup><font style="color:rgb(27, 28, 29);">。这包括： </font>
    - <font style="color:rgb(27, 28, 29);">概念性问题。</font>
    - <font style="color:rgb(27, 28, 29);">实践性C语言编程练习。</font>
    - <font style="color:rgb(27, 28, 29);">Shell脚本任务。</font>
    - <font style="color:rgb(27, 28, 29);">调试练习（学生在提供的代码片段中查找并修复错误）。</font>
+ <font style="color:rgb(27, 28, 29);">LLM可以被提示根据教科书中的自然语言描述生成特定语言的代码（例如，用于嵌入式Linux的C语言）</font><sup>**<font style="color:rgb(87, 91, 95);">7</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

#### <font style="color:rgb(27, 28, 29);">4.5.2. 编程练习的自动化功能正确性测试：</font>
+ <font style="color:rgb(27, 28, 29);">对于AI生成或学生提交的编程练习，实施自动化的功能正确性测试。</font>
+ <font style="color:rgb(27, 28, 29);">这涉及使用LLM（或由LLM指导的专用工具）为代码生成单元测试</font><sup>**<font style="color:rgb(87, 91, 95);">74</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">然后，系统将针对学生的代码执行这些单元测试，以提供关于正确性的即时、客观反馈。</font>
+ <font style="color:rgb(27, 28, 29);">像TypeTest这样的框架</font><sup>**<font style="color:rgb(87, 91, 95);">76</font>**</sup><font style="color:rgb(27, 28, 29);">通过推断类型并使用RAG获取更好的上下文来增强动态类型语言的测试生成。</font>

<font style="color:rgb(27, 28, 29);">不仅自动化编程练习的生成，还通过单元测试自动化其评估，显著增强了“考核内容生成”和“自动化检测”的能力，为编程等实践技能发展提供了强大的解决方案。竞赛要求生成多样化的评估，包括编程。LLM可以生成代码</font><sup>**<font style="color:rgb(87, 91, 95);">7</font>**</sup><font style="color:rgb(27, 28, 29);">，也可以被提示根据教科书创建基于此能力的练习</font><sup>**<font style="color:rgb(87, 91, 95);">67</font>**</sup><font style="color:rgb(27, 28, 29);">。手动评估代码对教师来说非常耗时。因此需要自动化评估。单元测试是代码正确性的标准。LLM也可以生成单元测试</font><sup>**<font style="color:rgb(87, 91, 95);">74</font>**</sup><font style="color:rgb(27, 28, 29);">。因此，系统可以实现一个端到端的流程：LLM从Linux教科书生成编程练习 -> 学生提交代码 -> 另一个LLM（或LLM指导的工具）为该特定练习生成单元测试 -> 系统运行测试 -> 学生获得关于功能正确性的即时反馈。这对于计算机科学教育来说是一个高度创新和实用的功能。</font>

### <font style="color:rgb(27, 28, 29);">4.6. 人机协作与内容进化</font>
#### <font style="color:rgb(27, 28, 29);">4.6.1. 教师在环（HITL）用于内容共创、优化与验证：</font>
+ <font style="color:rgb(27, 28, 29);">实施HITL工作流程，教师可以审查、编辑和批准AI生成的教育内容（教案、评估）</font><sup>**<font style="color:rgb(87, 91, 95);">5</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">教师的反馈应用于迭代优化AI的内容生成能力，形成持续改进的循环</font><sup>**<font style="color:rgb(87, 91, 95);">6</font>**</sup><font style="color:rgb(27, 28, 29);">。像TeacherMatic这样的平台</font><sup>**<font style="color:rgb(87, 91, 95);">80</font>**</sup><font style="color:rgb(27, 28, 29);">是为教师协作而设计的AI工具的典范。</font>

#### <font style="color:rgb(27, 28, 29);">4.6.2. 教育者反馈用于LLM参数/策略优化的闭环机制：</font>
+ <font style="color:rgb(27, 28, 29);">探索教师提供反馈以直接影响LLM底层参数或生成策略的机制（尽管非专家直接调整参数很复杂，但反馈可以指导微调或RLHF过程）。</font>
+ <font style="color:rgb(27, 28, 29);">像Dolphin（用于研究）</font><sup>**<font style="color:rgb(87, 91, 95);">83</font>**</sup><font style="color:rgb(27, 28, 29);">和提议的教师培训平台</font><sup>**<font style="color:rgb(87, 91, 95);">84</font>**</sup><font style="color:rgb(27, 28, 29);">这样的系统使用反馈循环来优化AI行为。这可能涉及教师对生成内容的质量或辅导互动有效性进行评级，并将这些数据用于进一步的模型对齐或微调。</font>

<font style="color:rgb(27, 28, 29);">真正的人机协作超越了简单的内容编辑。使教师能够影响AI的核心行为，即使是通过结构化反馈间接影响再训练或强化学习，也能导向一个真正从专家教学法中学习的系统。HITL对于AI生成内容的质量控制至关重要</font><sup>**<font style="color:rgb(87, 91, 95);">5</font>**</sup><font style="color:rgb(27, 28, 29);">，教师是领域专家。简单的编辑改进了单个内容片段。然而，为了让AI从教师的专业知识中</font>_<font style="color:rgb(27, 28, 29);">学习</font>_<font style="color:rgb(27, 28, 29);">，反馈需要更深入。</font><sup>**<font style="color:rgb(87, 91, 95);">83</font>**</sup><font style="color:rgb(27, 28, 29);"> (Dolphin) 和 </font><sup>**<font style="color:rgb(87, 91, 95);">84</font>**</sup><font style="color:rgb(27, 28, 29);"> (教师培训平台) 提出了闭环系统，其中AI的行动被评估，并且这种评估为未来的AI行为/生成提供信息。在这个教育背景下，如果教师一致地将某些类型的AI生成的解释评为“混乱”或“无效”，这种结构化的反馈可以被收集起来。然后，这些收集到的数据（提示 + AI生成 + 教师评级/修正）可以用于：进一步微调LLM（例如，使其偏好教师评价高的解释风格）；作为RLHF的偏好数据，引导LLM产生符合教学法的输出。这就创建了一个系统，在这个系统中，AI不仅仅是产生内容供教师修改，而是从他们的专业知识中学习，以便随着时间的推移自主地产生更好的内容。</font>

### <font style="color:rgb(27, 28, 29);">4.7. 培养高阶思维能力</font>
#### <font style="color:rgb(27, 28, 29);">4.7.1. 设计互动以促进批判性思维和元认知：</font>
+ <font style="color:rgb(27, 28, 29);">辅导智能体的设计不应仅仅是提供答案，而应引导学生对材料进行批判性思考</font><sup>**<font style="color:rgb(87, 91, 95);">8</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">策略包括： </font>
    - <font style="color:rgb(27, 28, 29);">提出探索性问题而非直接给出答案（苏格拉底方法）。</font>
    - <font style="color:rgb(27, 28, 29);">鼓励学生评估AI生成的信息，并识别潜在的错误或偏见</font><sup>**<font style="color:rgb(87, 91, 95);">8</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - <font style="color:rgb(27, 28, 29);">提示学生解释其推理过程（元认知）。</font>
    - <font style="color:rgb(27, 28, 29);">使用AI生成场景或数据供学生分析和解读</font><sup>**<font style="color:rgb(87, 91, 95);">86</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

<font style="color:rgb(27, 28, 29);">如果AI辅导员被设计成能够适当地挑战学生，而不是一个简单的答案机器，那么它可以成为批判性思维的催化剂。这符合现代教学目标。一个普遍的担忧是AI辅导员可能会使学生成为被动的学习者。教育的目标不仅仅是知识获取，还包括批判性思维的发展。研究</font><sup>**<font style="color:rgb(87, 91, 95);">8</font>**</sup><font style="color:rgb(27, 28, 29);">提出了使用AI</font>_<font style="color:rgb(27, 28, 29);">促进</font>_<font style="color:rgb(27, 28, 29);">批判性思维的具体策略。这些策略包括提示学生质疑AI的输出，验证信息，并参与苏格拉底式对话。因此，辅导智能体的设计应明确包含这些策略。例如，在提供解释后，它可以问：“你能想到一个这可能不适用的情况吗？”或“你将如何使用课程材料来验证这些信息？”这将AI从一个单纯的信息提供者转变为一个认知伙伴。</font>

## <font style="color:rgb(27, 28, 29);">第5部分：负责任的AI与伦理框架</font>
### <font style="color:rgb(27, 28, 29);">5.1. 偏见检测与缓解策略</font>
<font style="color:rgb(27, 28, 29);">实施多方面方法来识别和减轻LLM及其生成内容中的偏见（例如，性别、文化、社会经济偏见）</font><sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

+ **<font style="color:rgb(27, 28, 29);">数据层面：</font>**
    - <font style="color:rgb(27, 28, 29);">在使用本地知识库（“《嵌入式Linux开发实践教程》课件资料”）进行微调或RAG之前，对其进行现有偏见的审计</font><sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup><font style="color:rgb(27, 28, 29);">。这可能涉及使用NLP工具扫描刻板印象语言或有偏见的表述。</font>
    - <font style="color:rgb(27, 28, 29);">如果在源材料中发现偏见，考虑使用反事实示例进行数据增强或过滤有问题的内容</font><sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">模型层面（微调期间）：</font>**
    - <font style="color:rgb(27, 28, 29);">如果偏见被继承或放大，采用公平性感知的微调技术</font><sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - <font style="color:rgb(27, 28, 29);">在针对特定领域教育语料库进行微调时，减轻偏见继承</font><sup>**<font style="color:rgb(87, 91, 95);">96</font>**</sup><font style="color:rgb(27, 28, 29);">。可以探索基于令牌的指示符、基于掩码的缓解或专门的损失函数等策略</font><sup>**<font style="color:rgb(87, 91, 95);">97</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">输出层面（后处理与交互）：</font>**
    - <font style="color:rgb(27, 28, 29);">实施自我反思机制，让智能体批判自身输出的偏见</font><sup>**<font style="color:rgb(87, 91, 95);">91</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - <font style="color:rgb(27, 28, 29);">使用协作式偏见缓解，即多个智能体通过共识来辩论和减轻偏见</font><sup>**<font style="color:rgb(87, 91, 95);">88</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - <font style="color:rgb(27, 28, 29);">结合教师审查的HITL，以标记和纠正有偏见的输出</font><sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
    - <font style="color:rgb(27, 28, 29);">利用LLM即评判者（LLM-as-a-judge）方法评估响应的偏见</font><sup>**<font style="color:rgb(87, 91, 95);">93</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

<font style="color:rgb(27, 28, 29);">偏见缓解不是一次性任务，而是贯穿AI生命周期的持续过程。解决本地知识库本身的偏见是一个经常被忽视的关键预防步骤。LLM可能从其训练数据中继承并放大偏见</font><sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup><font style="color:rgb(27, 28, 29);">。系统使用本地知识库进行微调和RAG，而这个本地知识库是一本教科书。教科书，特别是较旧的或来自特定文化背景的教科书，可能包含其自身的偏见（角色中的性别刻板印象、文化不敏感、社会经济假设）。如果LLM在这个有偏见的教科书数据上进行微调，或者如果RAG检索到有偏见的部分，那么AI的输出很可能也会有偏见。因此，一个关键且经常被低估的步骤是</font>_<font style="color:rgb(27, 28, 29);">审计教育源材料本身</font>_<font style="color:rgb(27, 28, 29);">是否存在偏见</font><sup>**<font style="color:rgb(87, 91, 95);">94</font>**</sup><font style="color:rgb(27, 28, 29);">（尽管这些文献讨论的是其他背景下的审计，但原理适用）。这是在LLM与数据交互之前采取的一项主动措施。相关技术可以包括使用NLP工具扫描教科书中的偏见语言、刻板印象关联或某些群体代表性不足等问题。审计结果可以为微调前的数据清理/增强提供信息。</font>

**<font style="color:rgb(27, 28, 29);">表5.1：LLM生命周期中的偏见缓解策略</font>**

| **<font style="color:rgb(27, 28, 29);">生命周期阶段</font>** | **<font style="color:rgb(27, 28, 29);">针对的特定偏见类型 (例如，性别、文化、社会经济)</font>** | **<font style="color:rgb(27, 28, 29);">缓解技术</font>** | **<font style="color:rgb(27, 28, 29);">描述</font>** | **<font style="color:rgb(27, 28, 29);">实施示例</font>** | **<font style="color:rgb(27, 28, 29);">相关研究支撑</font>** |
| --- | --- | --- | --- | --- | --- |
| <font style="color:rgb(27, 28, 29);">数据准备</font> | <font style="color:rgb(27, 28, 29);">性别、文化、社会经济</font> | <font style="color:rgb(27, 28, 29);">数据审计、数据过滤、数据增强 (反事实样本)</font> | <font style="color:rgb(27, 28, 29);">分析知识库以发现并处理已有偏见。</font> | <font style="color:rgb(27, 28, 29);">使用NLP工具扫描教科书，识别并修改性别刻板印象的描述。</font> | <sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">模型微调</font> | <font style="color:rgb(27, 28, 29);">继承性偏见、放大性偏见</font> | <font style="color:rgb(27, 28, 29);">公平性感知微调、偏见继承缓解技术 (基于令牌、掩码、损失函数)</font> | <font style="color:rgb(27, 28, 29);">在训练过程中调整模型以减少偏见输出。</font> | <font style="color:rgb(27, 28, 29);">在微调LLM时，加入惩罚性别偏见关联的损失项。</font> | <sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">内容生成/交互</font> | <font style="color:rgb(27, 28, 29);">实时产生的偏见</font> | <font style="color:rgb(27, 28, 29);">自我反思机制、协作式偏见缓解 (多智能体辩论)</font> | <font style="color:rgb(27, 28, 29);">智能体在生成内容或交互时主动识别和纠正偏见。</font> | <font style="color:rgb(27, 28, 29);">多个评估智能体对一个有争议的教学解释进行辩论，以达成更中立的表述。</font> | <sup>**<font style="color:rgb(87, 91, 95);">88</font>**</sup> |
| <font style="color:rgb(27, 28, 29);">后处理/监控</font> | <font style="color:rgb(27, 28, 29);">残余偏见、新出现的偏见</font> | <font style="color:rgb(27, 28, 29);">人在回路 (HITL) 教师审查、LLM即评判者</font> | <font style="color:rgb(27, 28, 29);">对模型输出进行人工或AI辅助的审查，以发现并修正偏见。</font> | <font style="color:rgb(27, 28, 29);">教师标记AI生成练习题中的文化不敏感内容，系统记录并用于未来改进；使用另一个LLM评估辅导对话的公平性。</font> | <sup>**<font style="color:rgb(87, 91, 95);">87</font>**</sup> |


### <font style="color:rgb(27, 28, 29);">5.2. 数据隐私、安全与学生安全设计</font>
+ <font style="color:rgb(27, 28, 29);">严格遵守学生数据处理的隐私协议，确保符合相关法规（例如，中国的相关法规是首要考虑，同时可参考FERPA、GDPR等国际标准）</font><sup>**<font style="color:rgb(87, 91, 95);">4</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">实施强大的安全措施，防止数据泄露和未经授权的访问。</font>
+ <font style="color:rgb(27, 28, 29);">确保AI生成的内容安全、适合年龄，并且不含有害信息。教师应能标记/报告有问题的内容。</font>

### <font style="color:rgb(27, 28, 29);">5.3. AI决策的透明度与可解释性</font>
+ <font style="color:rgb(27, 28, 29);">力求AI生成内容和提出建议的方式具有透明度。</font>
+ <font style="color:rgb(27, 28, 29);">KG-RAG（见4.4.4节）通过展示检索到的知识图谱路径，内在地支持可解释性</font><sup>**<font style="color:rgb(87, 91, 95);">27</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">在提供反馈或评估时，系统应尽可能为其结论提供清晰的理由。</font>

### <font style="color:rgb(27, 28, 29);">5.4. 学生与教师使用AI的伦理指南</font>
+ <font style="color:rgb(27, 28, 29);">制定并传达关于教师和学生应如何与AI系统互动的明确伦理指南</font><sup>**<font style="color:rgb(87, 91, 95);">8</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ <font style="color:rgb(27, 28, 29);">这包括关于学术诚信、负责任地使用AI生成内容、数据隐私意识以及对AI输出进行批判性评估的指导。</font>

## <font style="color:rgb(27, 28, 29);">第6部分：初步技术与部署考量</font>
### <font style="color:rgb(27, 28, 29);">6.1. 开源LLM与中文友好模型的利用</font>
+ <font style="color:rgb(27, 28, 29);">根据竞赛指南，优先选择在中文处理方面表现良好，并且能够本地部署或通过安全、可靠的API访问的开源LLM。</font>
+ <font style="color:rgb(27, 28, 29);">根据性能、资源需求、微调能力和社区支持等标准评估模型。</font>

### <font style="color:rgb(27, 28, 29);">6.2. 本地知识库实施（数据准备、索引）</font>
+ <font style="color:rgb(27, 28, 29);">100MB的本地知识库（“《嵌入式Linux开发实践教程》课件资料”）需要仔细准备： </font>
    - **<font style="color:rgb(27, 28, 29);">从PDF提取文本：</font>**<font style="color:rgb(27, 28, 29);"> 使用布局感知的PDF文本提取工具（例如</font><sup>**<font style="color:rgb(87, 91, 95);">3</font>**</sup><font style="color:rgb(27, 28, 29);">中提到的PDFMiner）准确地将教科书转换为可用格式。</font>
    - **<font style="color:rgb(27, 28, 29);">数据清洗和预处理：</font>**<font style="color:rgb(27, 28, 29);"> 清洗提取的文本，处理格式不一致问题。</font>
    - **<font style="color:rgb(27, 28, 29);">RAG的分块策略：</font>**<font style="color:rgb(27, 28, 29);"> 实施有效的分块策略（例如</font><sup>**<font style="color:rgb(87, 91, 95);">62</font>**</sup><font style="color:rgb(27, 28, 29);">中基于句子/段落边界的自适应分块），以平衡上下文保留和检索粒度。</font>
    - **<font style="color:rgb(27, 28, 29);">索引：</font>**<font style="color:rgb(27, 28, 29);"> 为RAG中的语义搜索创建向量嵌入。如果实施KG-RAG，这还包括从教科书构建KG（实体和关系提取）</font><sup>**<font style="color:rgb(87, 91, 95);">1</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>

<font style="color:rgb(27, 28, 29);">知识库准备的质量直接影响“关联性和准确性”的评分标准。处理不当的PDF可能导致RAG系统“垃圾进，垃圾出”。竞赛要求使用本地知识库，以“课件资料”形式提供，例如Linux教程的PDF。核心功能依赖此知识库进行信息 grounding。由于复杂的布局、图像、表格等，PDF对于直接的NLP处理来说非常困难，简单的文本提取通常会失败或混淆内容。因此，强大的PDF解析和布局感知的文本提取</font><sup>**<font style="color:rgb(87, 91, 95);">3</font>**</sup><font style="color:rgb(27, 28, 29);">是关键的第一步。文本提取后，需要为RAG适当地分块</font><sup>**<font style="color:rgb(87, 91, 95);">62</font>**</sup><font style="color:rgb(27, 28, 29);">。如果追求KG-RAG，则需要从这些文本中提取实体/关系以构建KG</font><sup>**<font style="color:rgb(87, 91, 95);">1</font>**</sup><font style="color:rgb(27, 28, 29);">。这些准备阶段的任何失败都将严重降低RAG系统的性能，从而影响AI的整体准确性和相关性，直接影响关键的评分标准。</font>

### <font style="color:rgb(27, 28, 29);">6.3. 开发环境与技术栈建议</font>
+ <font style="color:rgb(27, 28, 29);">虽然允许灵活性，但可以根据LLM应用开发的常见实践提出建议（例如，Python，流行的机器学习框架如PyTorch/TensorFlow，用于RAG的向量数据库，用于KG的图数据库）。</font>
+ <font style="color:rgb(27, 28, 29);">如果追求多智能体架构，考虑使用相应的框架（例如</font><sup>**<font style="color:rgb(87, 91, 95);">15</font>**</sup><font style="color:rgb(27, 28, 29);">中的LangGraph，或来自Azure AI Foundry Agents </font><sup>**<font style="color:rgb(87, 91, 95);">12</font>**</sup><font style="color:rgb(27, 28, 29);">的概念）。</font>

## <font style="color:rgb(27, 28, 29);">第7部分：评估计划与指标</font>
### <font style="color:rgb(27, 28, 29);">7.1. 对齐竞赛评分标准</font>
+ **<font style="color:rgb(27, 28, 29);">功能完整度 (40分)：</font>**<font style="color:rgb(27, 28, 29);"> 系统性测试所有为教师、学生和管理员指定的核心功能。</font>
+ **<font style="color:rgb(27, 28, 29);">技术与创新性 (30分)：</font>**
    - **<font style="color:rgb(27, 28, 29);">LLM集成合理性 (10分)：</font>**<font style="color:rgb(27, 28, 29);"> 证明LLM选择的合理性，展示其与其他组件（例如，MAS、RAG）的有效集成。</font>
    - **<font style="color:rgb(27, 28, 29);">本地知识库应用的准确性 (10分)：</font>**<font style="color:rgb(27, 28, 29);"> 衡量输出相对于本地知识库的相关性和准确性。这涉及设计特定的测试用例来验证 grounding（例如，提出答案仅存在于知识库中的问题，并检查是否存在幻觉或不相关信息）。</font>
    - **<font style="color:rgb(27, 28, 29);">模型微调创新性 (10分)：</font>**<font style="color:rgb(27, 28, 29);"> 展示创新的微调方法（例如，CFT、针对教育领域定制的指令微调、基于RL的优化）及其影响。</font>
+ **<font style="color:rgb(27, 28, 29);">教育实用与创新性 (20分)：</font>**
    - <font style="color:rgb(27, 28, 29);">根据评分标准进行专家评估，评判系统在教育领域的实用价值。这包括展示系统如何解决实际教学/学习痛点，并提供新颖的教学益处（例如，个性化路径、批判性思维促进、高效评估）。</font>
+ **<font style="color:rgb(27, 28, 29);">文档质量 (10分)：</font>**<font style="color:rgb(27, 28, 29);"> 确保所有提交的文档（设计文档、用户手册、PPT）清晰、结构合理，并准确反映系统。</font>

### <font style="color:rgb(27, 28, 29);">7.2. AI性能、教学有效性与用户满意度指标</font>
+ **<font style="color:rgb(27, 28, 29);">AI性能：</font>**
    - <font style="color:rgb(27, 28, 29);">问答响应准确率（例如，F1分数，适用时使用ROUGE/BLEU评估生成文本，对照KG的正确性）。</font>
    - <font style="color:rgb(27, 28, 29);">代码生成正确性（例如，单元测试通过率）。</font>
    - <font style="color:rgb(27, 28, 29);">实时交互的延迟。</font>
    - <font style="color:rgb(27, 28, 29);">对于复杂评估（例如，编码、案例研究）的评估，考虑采用LLM即评判者的方法，并结合人工/专家反馈进行细致评分</font><sup>**<font style="color:rgb(87, 91, 95);">98</font>**</sup><font style="color:rgb(27, 28, 29);">。</font>
+ **<font style="color:rgb(27, 28, 29);">教学有效性：</font>**
    - <font style="color:rgb(27, 28, 29);">衡量学生学习成果的改善（例如，在竞赛约束允许的情况下，对系统覆盖的概念进行前后测验得分比较）。</font>
    - <font style="color:rgb(27, 28, 29);">追踪学生参与度指标（例如，任务花费时间、互动频率）。</font>
    - <font style="color:rgb(27, 28, 29);">教师效率提升（例如，根据系统日志或调查，备课或评分时间减少）。</font>
+ **<font style="color:rgb(27, 28, 29);">用户满意度：</font>**
    - <font style="color:rgb(27, 28, 29);">通过调查或可用性测试，收集模拟用户（教师、学生）的反馈，重点关注易用性、感知效用和整体体验。</font>

<font style="color:rgb(27, 28, 29);">评估必须是多方面的，涵盖技术AI性能、实际教育效益和用户体验，同时直接对应竞赛的具体评分标准。竞赛有明确的评分规则，评估计划</font>_<font style="color:rgb(27, 28, 29);">必须</font>_<font style="color:rgb(27, 28, 29);">直接针对每个项目。“功能完整度”要求测试所有指定功能。“技术与创新性”要求展示LLM选择、知识库 grounding 和微调的价值。这意味着需要针对知识库的准确性进行特定测试，如果探索了不同的微调方法，则需要进行消融研究或比较。“教育实用与创新性”更偏向定性，但可以通过关于学习收益或效率的量化数据来支持，并通过展示创新功能（如基于ZPD的路径或批判性思维提示）如何解决实际教育问题来体现。除了评分规则之外，还需要标准的AI和UX指标来确保系统稳健且可用。这包括LLM性能指标（准确性、延迟）和教学指标（学习成果、参与度）。对于自动化编码评估等高级功能，LLM即评判者</font><sup>**<font style="color:rgb(87, 91, 95);">98</font>**</sup><font style="color:rgb(27, 28, 29);">等专门评估方法对于评估LLM生成的反馈或分数的质量变得尤为重要，尤其是在明确的对/错答案不足以衡量时。</font>

## <font style="color:rgb(27, 28, 29);">第8部分：结论与未来展望</font>
<font style="color:rgb(27, 28, 29);">本智能教学实训智能体软件通过整合先进的AI技术，特别是基于开源大型语言模型的多智能体架构、个性化学习路径、以及与本地知识库深度融合的检索增强生成和上下文微调技术，旨在显著提升教学效率和学生学习效果。系统不仅满足了竞赛提出的核心功能需求，如智能备课、智能考核、学情分析、实时辅导等，更在技术创新和教育实用性方面提出了诸多前瞻性的解决方案，例如动态教学角色、基于ZPD的自适应学习、知识图谱增强的可解释反馈，以及促进高阶思维能力的互动设计。</font>

<font style="color:rgb(27, 28, 29);">通过对教师赋能和学生个性化支持，本系统有望解决传统实训教学模式中的诸多痛点，推动教育数字化从理论走向规模化实践。其对负责任AI原则的遵循，包括偏见检测与缓解、数据隐私保护和伦理指南的制定，确保了技术的健康发展和应用。</font>

<font style="color:rgb(27, 28, 29);">展望未来，该系统具备向更广泛学科领域拓展的潜力，可以进一步深化与现有教育信息系统的集成，并作为平台进行更前沿的AI驱动教学法研究。其模块化和可扩展的设计，为持续吸纳新兴AI技术、不断优化教学策略奠定了坚实基础，有望为国家教育数字化战略贡献一个具有示范意义的智能化解决方案，最终支撑高素质应用型人才的培养。</font>

<font style="color:rgb(27, 28, 29);"></font>

---

# <font style="color:rgb(27, 28, 29);">数据字典与数据库设计 </font>
<font style="color:rgb(27, 28, 29);">好的，这是一个基于您提供的详细软件需求规格说明书（SRS）设计的数据字典和数据库E-R图（概念模型）。</font>

**<font style="color:rgb(27, 28, 29);">核心设计原则：</font>**

1. **<font style="color:rgb(27, 28, 29);">模块化：</font>**<font style="color:rgb(27, 28, 29);"> 对应教师、学生、管理员三大模块，以及AI核心引擎和知识库。</font>
2. **<font style="color:rgb(27, 28, 29);">用户中心：</font>**<font style="color:rgb(27, 28, 29);"> 所有活动和数据都与特定用户角色关联。</font>
3. **<font style="color:rgb(27, 28, 29);">内容关联：</font>**<font style="color:rgb(27, 28, 29);"> 教学内容、考核、练习等紧密关联。</font>
4. **<font style="color:rgb(27, 28, 29);">可追溯性与分析：</font>**<font style="color:rgb(27, 28, 29);"> 记录学生行为、教师操作，支持学情分析和系统优化。</font>
5. **<font style="color:rgb(27, 28, 29);">扩展性：</font>**<font style="color:rgb(27, 28, 29);"> 为未来的高级功能（如MAS、KG、RL）预留接口或设计考量。</font>

---

**<font style="color:rgb(27, 28, 29);">数据字典</font>**

<font style="color:rgb(27, 28, 29);">为简洁起见，这里列出核心表及其关键字段。实际实现中可能需要更多审计字段（如 </font>`<font style="color:rgb(87, 91, 95);">created_by</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">updated_by</font>`<font style="color:rgb(27, 28, 29);">）和配置表。</font>

**<font style="color:rgb(27, 28, 29);">1. 用户与权限 (User & Access Control)</font>**

+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">Users</font>**`**<font style="color:rgb(27, 28, 29);"> (用户信息表)</font>**
    - `<font style="color:rgb(87, 91, 95);">UserID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 用户唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">Username</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Unique, Not Null): 用户名 (登录用)</font>
    - `<font style="color:rgb(87, 91, 95);">PasswordHash</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Not Null): 加密后的密码</font>
    - `<font style="color:rgb(87, 91, 95);">FullName</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255)): 用户真实姓名</font>
    - `<font style="color:rgb(87, 91, 95);">Email</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Unique): 电子邮箱</font>
    - `<font style="color:rgb(87, 91, 95);">Role</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Admin', 'Teacher', 'Student'), Not Null): 用户角色</font>
    - `<font style="color:rgb(87, 91, 95);">RegistrationDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 注册日期</font>
    - `<font style="color:rgb(87, 91, 95);">LastLoginDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Nullable): 最后登录日期</font>
    - `<font style="color:rgb(87, 91, 95);">IsActive</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Default true): 账户是否激活</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">Roles</font>**`**<font style="color:rgb(27, 28, 29);"> (角色表 - 可选，如果权限复杂)</font>**
    - `<font style="color:rgb(87, 91, 95);">RoleID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 角色唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">RoleName</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(50), Unique, Not Null): 角色名称 (Admin, Teacher, Student)</font>
    - `<font style="color:rgb(87, 91, 95);">Description</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 角色描述</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">Permissions</font>**`**<font style="color:rgb(27, 28, 29);"> (权限表 - 可选，如果权限复杂)</font>**
    - `<font style="color:rgb(87, 91, 95);">PermissionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 权限唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">PermissionName</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(100), Unique, Not Null): 权限名称 (e.g., 'CreateCourse', 'SubmitAssignment')</font>
    - `<font style="color:rgb(87, 91, 95);">Description</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 权限描述</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">RolePermissions</font>**`**<font style="color:rgb(27, 28, 29);"> (角色权限关联表 - 可选)</font>**
    - `<font style="color:rgb(87, 91, 95);">RoleID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Roles.RoleID</font>`<font style="color:rgb(27, 28, 29);">, PK): 角色ID</font>
    - `<font style="color:rgb(87, 91, 95);">PermissionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Permissions.PermissionID</font>`<font style="color:rgb(27, 28, 29);">, PK): 权限ID</font>

**<font style="color:rgb(27, 28, 29);">2. 课程与教学内容 (Curriculum & Content)</font>**

+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">KnowledgeBaseArticles</font>**`**<font style="color:rgb(27, 28, 29);"> (知识库文章表)</font>**
    - `<font style="color:rgb(87, 91, 95);">ArticleID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 文章唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">Title</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Not Null): 文章标题</font>
    - `<font style="color:rgb(87, 91, 95);">Content</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Not Null): 文章原始内容 (清洗后的文本)</font>
    - `<font style="color:rgb(87, 91, 95);">OriginalFilePath</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(512), Nullable): 原始文件路径 (e.g., '《嵌入式Linux开发实践教程》/chapter1.pdf')</font>
    - `<font style="color:rgb(87, 91, 95);">SourceDocumentName</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Nullable): 来源文档名称 (e.g., '《嵌入式Linux开发实践教程》')</font>
    - `<font style="color:rgb(87, 91, 95);">ExtractedText</font>`<font style="color:rgb(27, 28, 29);"> (LONGTEXT, Nullable): 从PDF等提取的纯文本</font>
    - `<font style="color:rgb(87, 91, 95);">ChunkedContent</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): RAG分块内容 (每个块含文本和元数据)</font>
    - `<font style="color:rgb(87, 91, 95);">VectorEmbedding</font>`<font style="color:rgb(27, 28, 29);"> (BLOB or TEXT, Nullable): 文本块的向量表示 (或指向向量库的ID)</font>
    - `<font style="color:rgb(87, 91, 95);">Keywords</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): 关键词列表 (e.g., </font>`<font style="color:rgb(87, 91, 95);">['Linux', 'Kernel', 'GCC']</font>`<font style="color:rgb(27, 28, 29);">)</font>
    - `<font style="color:rgb(87, 91, 95);">UploadedByAdminID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 上传管理员ID</font>
    - `<font style="color:rgb(87, 91, 95);">UploadTimestamp</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 上传时间</font>
    - `<font style="color:rgb(87, 91, 95);">LastModifiedTimestamp</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP): 最后修改时间</font>
    - `<font style="color:rgb(87, 91, 95);">KG_Entities_Relations</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): 若构建KG，存储相关的实体和关系摘要</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">Courses</font>**`**<font style="color:rgb(27, 28, 29);"> (课程表)</font>**
    - `<font style="color:rgb(87, 91, 95);">CourseID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 课程唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">CourseName</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Not Null): 课程名称</font>
    - `<font style="color:rgb(87, 91, 95);">Description</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 课程描述</font>
    - `<font style="color:rgb(87, 91, 95);">TeacherID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 创建课程的教师ID</font>
    - `<font style="color:rgb(87, 91, 95);">Syllabus</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 课程大纲 (AI生成或教师上传)</font>
    - `<font style="color:rgb(87, 91, 95);">CreationDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 创建日期</font>
    - `<font style="color:rgb(87, 91, 95);">LastUpdateDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP): 最后更新日期</font>
    - `<font style="color:rgb(87, 91, 95);">Status</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Draft', 'Published', 'Archived'), Default 'Draft'): 课程状态</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">TeachingPlans</font>**`**<font style="color:rgb(27, 28, 29);"> (教案/教学单元表)</font>**
    - `<font style="color:rgb(87, 91, 95);">PlanID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 教案唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">CourseID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Courses.CourseID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 所属课程ID</font>
    - `<font style="color:rgb(87, 91, 95);">Title</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Not Null): 教案/单元标题</font>
    - `<font style="color:rgb(87, 91, 95);">SequenceOrder</font>`<font style="color:rgb(27, 28, 29);"> (INT, Not Null): 在课程中的顺序</font>
    - `<font style="color:rgb(87, 91, 95);">LearningObjectives</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 学习目标</font>
    - `<font style="color:rgb(87, 91, 95);">Content</font>`<font style="color:rgb(27, 28, 29);"> (LONGTEXT, Nullable): 教学内容 (AI生成，教师编辑)</font>
    - `<font style="color:rgb(87, 91, 95);">EstimatedDurationMinutes</font>`<font style="color:rgb(27, 28, 29);"> (INT, Nullable): 预计时长 (分钟)</font>
    - `<font style="color:rgb(87, 91, 95);">RelatedKnowledgePoints</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): 关联知识点 (可关联 </font>`<font style="color:rgb(87, 91, 95);">KnowledgeBaseArticles.ArticleID</font>`<font style="color:rgb(27, 28, 29);"> 或 </font>`<font style="color:rgb(87, 91, 95);">Skills.SkillID</font>`<font style="color:rgb(27, 28, 29);">)</font>
    - `<font style="color:rgb(87, 91, 95);">Status</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Draft', 'Reviewed', 'Published'), Default 'Draft'): 教案状态</font>
    - `<font style="color:rgb(87, 91, 95);">AIGenerated</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Default true): 是否由AI初步生成</font>
    - `<font style="color:rgb(87, 91, 95);">TeacherFeedbackOnAI</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 教师对AI生成内容的反馈</font>
    - `<font style="color:rgb(87, 91, 95);">CreationDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 创建日期</font>
    - `<font style="color:rgb(87, 91, 95);">LastModifiedDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP): 最后修改日期</font>

**<font style="color:rgb(27, 28, 29);">3. 考核与练习 (Assessments & Exercises)</font>**

+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">Assessments</font>**`**<font style="color:rgb(27, 28, 29);"> (考核/练习集表)</font>**
    - `<font style="color:rgb(87, 91, 95);">AssessmentID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 考核唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">CourseID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Courses.CourseID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 所属课程ID (如果是课程级考核)</font>
    - `<font style="color:rgb(87, 91, 95);">PlanID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">TeachingPlans.PlanID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 所属教案ID (如果是单元练习)</font>
    - `<font style="color:rgb(87, 91, 95);">Title</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Not Null): 考核标题</font>
    - `<font style="color:rgb(87, 91, 95);">Type</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Quiz', 'Homework', 'Exam', 'Practice'), Not Null): 考核类型</font>
    - `<font style="color:rgb(87, 91, 95);">Instructions</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 考核说明</font>
    - `<font style="color:rgb(87, 91, 95);">DifficultyLevel</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Easy', 'Medium', 'Hard'), Nullable): 难度级别</font>
    - `<font style="color:rgb(87, 91, 95);">TimeLimitMinutes</font>`<font style="color:rgb(27, 28, 29);"> (INT, Nullable): 时长限制 (分钟)</font>
    - `<font style="color:rgb(87, 91, 95);">CreatedByTeacherID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 创建教师ID</font>
    - `<font style="color:rgb(87, 91, 95);">AIGenerated</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Default false): 是否由AI初步生成</font>
    - `<font style="color:rgb(87, 91, 95);">CreationDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 创建日期</font>
    - `<font style="color:rgb(87, 91, 95);">DueDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Nullable): 截止日期</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">Questions</font>**`**<font style="color:rgb(27, 28, 29);"> (题目表)</font>**
    - `<font style="color:rgb(87, 91, 95);">QuestionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 题目唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">AssessmentID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Assessments.AssessmentID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 所属考核ID (若题目属于特定考核)</font>
    - `<font style="color:rgb(87, 91, 95);">KnowledgePointSourceID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">KnowledgeBaseArticles.ArticleID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 题目关联的知识库来源</font>
    - `<font style="color:rgb(87, 91, 95);">QuestionText</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Not Null): 题干内容</font>
    - `<font style="color:rgb(87, 91, 95);">QuestionType</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('MultipleChoice', 'SingleChoice', 'ShortAnswer', 'Programming', 'Essay'), Not Null): 题目类型</font>
    - `<font style="color:rgb(87, 91, 95);">DifficultyLevel</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Easy', 'Medium', 'Hard'), Default 'Medium'): 题目难度</font>
    - `<font style="color:rgb(87, 91, 95);">Points</font>`<font style="color:rgb(27, 28, 29);"> (DECIMAL(5,2), Default 0.00): 题目分值</font>
    - `<font style="color:rgb(87, 91, 95);">ReferenceAnswer</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 参考答案 (对简答、编程题)</font>
    - `<font style="color:rgb(87, 91, 95);">SolutionExplanation</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 答案解析</font>
    - `<font style="color:rgb(87, 91, 95);">AIGenerated</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Default true): 是否由AI初步生成</font>
    - `<font style="color:rgb(87, 91, 95);">TeacherReviewed</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Default false): 教师是否审查通过</font>
    - `<font style="color:rgb(87, 91, 95);">CreatorID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 创建者 (教师或系统)</font>
    - `<font style="color:rgb(87, 91, 95);">CreationDate</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 创建日期</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">QuestionOptions</font>**`**<font style="color:rgb(27, 28, 29);"> (选择题选项表)</font>**
    - `<font style="color:rgb(87, 91, 95);">OptionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 选项唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">QuestionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Questions.QuestionID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 所属题目ID</font>
    - `<font style="color:rgb(87, 91, 95);">OptionText</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Not Null): 选项内容</font>
    - `<font style="color:rgb(87, 91, 95);">IsCorrect</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Not Null): 是否为正确答案</font>
    - `<font style="color:rgb(87, 91, 95);">DisplayOrder</font>`<font style="color:rgb(27, 28, 29);"> (INT, Nullable): 显示顺序</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">StudentSubmissions</font>**`**<font style="color:rgb(27, 28, 29);"> (学生提交表)</font>**
    - `<font style="color:rgb(87, 91, 95);">SubmissionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 提交唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">AssessmentID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Assessments.AssessmentID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 所属考核ID</font>
    - `<font style="color:rgb(87, 91, 95);">StudentID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 提交学生ID</font>
    - `<font style="color:rgb(87, 91, 95);">SubmissionTimestamp</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 提交时间</font>
    - `<font style="color:rgb(87, 91, 95);">IsLate</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Default false): 是否迟交</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">StudentAnswers</font>**`**<font style="color:rgb(27, 28, 29);"> (学生答案详情表)</font>**
    - `<font style="color:rgb(87, 91, 95);">AnswerID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 答案唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">SubmissionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">StudentSubmissions.SubmissionID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 所属提交ID</font>
    - `<font style="color:rgb(87, 91, 95);">QuestionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Questions.QuestionID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 对应题目ID</font>
    - `<font style="color:rgb(87, 91, 95);">AnswerContent</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 学生答案内容 (JSON for MC/SC, Text for SA/Essay/Code)</font>
    - `<font style="color:rgb(87, 91, 95);">AutoScore</font>`<font style="color:rgb(27, 28, 29);"> (DECIMAL(5,2), Nullable): 自动评分数</font>
    - `<font style="color:rgb(87, 91, 95);">AutoFeedback</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 自动评语/错误定位</font>
    - `<font style="color:rgb(87, 91, 95);">TeacherScore</font>`<font style="color:rgb(27, 28, 29);"> (DECIMAL(5,2), Nullable): 教师评分数</font>
    - `<font style="color:rgb(87, 91, 95);">TeacherFeedback</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 教师评语</font>
    - `<font style="color:rgb(87, 91, 95);">IsCorrect_Auto</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Nullable): 自动判断是否正确</font>
    - `<font style="color:rgb(87, 91, 95);">IsCorrect_Teacher</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Nullable): 教师判断是否正确</font>

**<font style="color:rgb(27, 28, 29);">4. 学生学习与互动 (Student Learning & Interaction)</font>**

+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">StudentLearningProgress</font>**`**<font style="color:rgb(27, 28, 29);"> (学生学习进度表)</font>**
    - `<font style="color:rgb(87, 91, 95);">ProgressID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 进度唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">StudentID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 学生ID</font>
    - `<font style="color:rgb(87, 91, 95);">CourseID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Courses.CourseID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 课程ID</font>
    - `<font style="color:rgb(87, 91, 95);">PlanID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">TeachingPlans.PlanID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 教案/单元ID</font>
    - `<font style="color:rgb(87, 91, 95);">KnowledgePointID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">KnowledgeBaseArticles.ArticleID</font>`<font style="color:rgb(27, 28, 29);"> or </font>`<font style="color:rgb(87, 91, 95);">Skills.SkillID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 细化到知识点/技能</font>
    - `<font style="color:rgb(87, 91, 95);">MasteryLevel</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(50), Nullable): 掌握程度 (e.g., 'NotStarted', 'InProgress', 'Mastered', ZPD_Level_X)</font>
    - `<font style="color:rgb(87, 91, 95);">LastAccessedTimestamp</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Nullable): 最后访问时间</font>
    - `<font style="color:rgb(87, 91, 95);">TimeSpentSeconds</font>`<font style="color:rgb(27, 28, 29);"> (INT, Default 0): 累计学习时长 (秒)</font>
    - `<font style="color:rgb(87, 91, 95);">EngagementScore</font>`<font style="color:rgb(27, 28, 29);"> (DECIMAL(3,2), Nullable): 参与度评分 (基于互动等计算)</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">StudentQueries</font>**`**<font style="color:rgb(27, 28, 29);"> (学生问答助手交互日志)</font>**
    - `<font style="color:rgb(87, 91, 95);">QueryID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 查询唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">StudentID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 学生ID</font>
    - `<font style="color:rgb(87, 91, 95);">SessionID</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Nullable): 对话会话ID (用于长期对话)</font>
    - `<font style="color:rgb(87, 91, 95);">QueryText</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Not Null): 学生提问内容</font>
    - `<font style="color:rgb(87, 91, 95);">AIResponse</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): AI助手回答内容</font>
    - `<font style="color:rgb(87, 91, 95);">ResponseTimestamp</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 回答时间</font>
    - `<font style="color:rgb(87, 91, 95);">RelevantSources</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): AI回答引用的知识库来源 (</font>`<font style="color:rgb(87, 91, 95);">ArticleID</font>`<font style="color:rgb(27, 28, 29);">列表)</font>
    - `<font style="color:rgb(87, 91, 95);">HelpfulnessRating</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Helpful', 'SomewhatHelpful', 'NotHelpful'), Nullable): 学生对回答的评价</font>
    - `<font style="color:rgb(87, 91, 95);">CoT_ToT_Path</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): (高级功能) 思维链/思维树路径记录</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">StudentPracticeHistory</font>**`**<font style="color:rgb(27, 28, 29);"> (学生个性化练习历史)</font>**
    - `<font style="color:rgb(87, 91, 95);">PracticeID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 练习记录标识</font>
    - `<font style="color:rgb(87, 91, 95);">StudentID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 学生ID</font>
    - `<font style="color:rgb(87, 91, 95);">QuestionID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Questions.QuestionID</font>`<font style="color:rgb(27, 28, 29);">, Not Null): 练习的题目ID</font>
    - `<font style="color:rgb(87, 91, 95);">AttemptTimestamp</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 尝试时间</font>
    - `<font style="color:rgb(87, 91, 95);">StudentAnswer</font>`<font style="color:rgb(27, 28, 29);"> (TEXT): 学生答案</font>
    - `<font style="color:rgb(87, 91, 95);">IsCorrect</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Nullable): 是否正确</font>
    - `<font style="color:rgb(87, 91, 95);">FeedbackProvided</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 提供的即时反馈</font>

**<font style="color:rgb(27, 28, 29);">5. AI核心与系统管理 (AI Core & System Management)</font>**

+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">LLMModels</font>**`**<font style="color:rgb(27, 28, 29);"> (大语言模型配置表)</font>**
    - `<font style="color:rgb(87, 91, 95);">ModelID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 模型唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">ModelName</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Not Null): 模型名称 (e.g., 'Qwen-7B-Chat', 'ChatGLM3-6B')</font>
    - `<font style="color:rgb(87, 91, 95);">Version</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(50), Nullable): 模型版本</font>
    - `<font style="color:rgb(87, 91, 95);">APIBaseURL</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(512), Nullable): API访问地址 (若通过API)</font>
    - `<font style="color:rgb(87, 91, 95);">APIKey</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(512), Nullable): API密钥 (加密存储)</font>
    - `<font style="color:rgb(87, 91, 95);">DeploymentType</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Local', 'API'), Not Null): 部署类型</font>
    - `<font style="color:rgb(87, 91, 95);">IsActive</font>`<font style="color:rgb(27, 28, 29);"> (BOOLEAN, Default true): 当前是否为活动模型</font>
    - `<font style="color:rgb(87, 91, 95);">Capabilities</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): 模型能力描述 (e.g., </font>`<font style="color:rgb(87, 91, 95);">{'code_generation': true, 'RAG_optimized': true}</font>`<font style="color:rgb(27, 28, 29);">)</font>
    - `<font style="color:rgb(87, 91, 95);">FineTuningDetails</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 微调相关信息 (数据集、参数等)</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">SystemLogs</font>**`**<font style="color:rgb(27, 28, 29);"> (系统日志表)</font>**
    - `<font style="color:rgb(87, 91, 95);">LogID</font>`<font style="color:rgb(27, 28, 29);"> (BIGINT, PK, AutoIncrement): 日志唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">Timestamp</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Default CURRENT_TIMESTAMP): 日志时间</font>
    - `<font style="color:rgb(87, 91, 95);">UserID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Users.UserID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 操作用户ID</font>
    - `<font style="color:rgb(87, 91, 95);">LogLevel</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('INFO', 'WARNING', 'ERROR', 'DEBUG'), Not Null): 日志级别</font>
    - `<font style="color:rgb(87, 91, 95);">ActivityType</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(100), Not Null): 活动类型 (e.g., 'Login', 'ContentGeneration', 'QuerySubmission', 'RAG_Retrieval')</font>
    - `<font style="color:rgb(87, 91, 95);">Message</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Not Null): 日志信息</font>
    - `<font style="color:rgb(87, 91, 95);">SourceIP</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(45), Nullable): 源IP地址</font>
    - `<font style="color:rgb(87, 91, 95);">Details</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): 额外细节 (e.g., 请求参数、响应摘要)</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">AgentConfigurations</font>**`**<font style="color:rgb(27, 28, 29);"> (多智能体配置表 - 高级功能)</font>**
    - `<font style="color:rgb(87, 91, 95);">AgentID</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(100), PK): 智能体唯一标识 (e.g., 'CurriculumAgent_Course123', 'TutoringAgent_Student456')</font>
    - `<font style="color:rgb(87, 91, 95);">AgentType</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('CurriculumAgent', 'AssessmentAgent', 'TutoringAgent', 'StudentModelingAgent', 'DialogManagementAgent', 'KnowledgeBaseAgent', 'SupervisorAgent'), Not Null): 智能体类型</font>
    - `<font style="color:rgb(87, 91, 95);">LLMModelID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">LLMModels.ModelID</font>`<font style="color:rgb(27, 28, 29);">, Nullable): 使用的LLM模型</font>
    - `<font style="color:rgb(87, 91, 95);">Configuration</font>`<font style="color:rgb(27, 28, 29);"> (JSON, Nullable): 特定智能体的配置参数 (e.g., prompts, RAG settings, ZPD thresholds)</font>
    - `<font style="color:rgb(87, 91, 95);">Status</font>`<font style="color:rgb(27, 28, 29);"> (ENUM('Active', 'Inactive', 'Error'), Default 'Active'): 智能体状态</font>
    - `<font style="color:rgb(87, 91, 95);">LastHeartbeat</font>`<font style="color:rgb(27, 28, 29);"> (TIMESTAMP, Nullable): 最后心跳时间</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">Skills</font>**`**<font style="color:rgb(27, 28, 29);"> (技能图谱节点 - 高级功能)</font>**
    - `<font style="color:rgb(87, 91, 95);">SkillID</font>`<font style="color:rgb(27, 28, 29);"> (INT, PK, AutoIncrement): 技能唯一标识</font>
    - `<font style="color:rgb(87, 91, 95);">SkillName</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(255), Not Null, Unique): 技能名称</font>
    - `<font style="color:rgb(87, 91, 95);">Description</font>`<font style="color:rgb(27, 28, 29);"> (TEXT, Nullable): 技能描述</font>
    - `<font style="color:rgb(87, 91, 95);">Domain</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(100), Nullable): 所属领域 (e.g., 'Embedded Linux - File Systems')</font>
+ **<font style="color:rgb(27, 28, 29);">Table: </font>**`**<font style="color:rgb(87, 91, 95);">SkillDependencies</font>**`**<font style="color:rgb(27, 28, 29);"> (技能依赖关系 - 高级功能)</font>**
    - `<font style="color:rgb(87, 91, 95);">PrerequisiteSkillID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Skills.SkillID</font>`<font style="color:rgb(27, 28, 29);">, PK): 前置技能ID</font>
    - `<font style="color:rgb(87, 91, 95);">PostrequisiteSkillID</font>`<font style="color:rgb(27, 28, 29);"> (INT, FK to </font>`<font style="color:rgb(87, 91, 95);">Skills.SkillID</font>`<font style="color:rgb(27, 28, 29);">, PK): 后置技能ID</font>
    - `<font style="color:rgb(87, 91, 95);">DependencyType</font>`<font style="color:rgb(27, 28, 29);"> (VARCHAR(50), Nullable): 依赖类型 (e.g., 'Requires', 'Suggests')</font>

---

**<font style="color:rgb(27, 28, 29);">数据库E-R图 (概念模型描述)</font>**

<font style="color:rgb(27, 28, 29);">由于文本格式难以绘制标准ER图，这里用文字描述主要实体及其关系：</font>

1. **<font style="color:rgb(27, 28, 29);">Users - Roles (可选)</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个角色可以有多个用户，一个用户只有一个角色)。</font>
2. **<font style="color:rgb(27, 28, 29);">Users (Teacher) - Courses</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个教师可以创建多个课程，一个课程由一个教师创建)。</font>
3. **<font style="color:rgb(27, 28, 29);">Courses - TeachingPlans</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个课程包含多个教案/单元，一个教案属于一个课程)。</font>
4. **<font style="color:rgb(27, 28, 29);">TeachingPlans - Assessments</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个教案可以关联多个考核/练习)。</font>
5. **<font style="color:rgb(27, 28, 29);">Courses - Assessments</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个课程也可以直接关联考核，如期末考试)。</font>
6. **<font style="color:rgb(27, 28, 29);">Assessments - Questions</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个考核包含多个题目)。</font>
7. **<font style="color:rgb(27, 28, 29);">Questions - QuestionOptions</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个选择题有多个选项)。</font>
8. **<font style="color:rgb(27, 28, 29);">Users (Teacher) - Assessments/Questions</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (教师创建考核和题目)。</font>
9. **<font style="color:rgb(27, 28, 29);">Users (Student) - StudentSubmissions</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个学生可以有多次提交)。</font>
10. **<font style="color:rgb(27, 28, 29);">Assessments - StudentSubmissions</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个考核可以被多名学生提交)。</font>
11. **<font style="color:rgb(27, 28, 29);">StudentSubmissions - StudentAnswers</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一次提交包含多个题目的答案)。</font>
12. **<font style="color:rgb(27, 28, 29);">Questions - StudentAnswers</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个题目可以有多个学生的答案记录)。</font>
13. **<font style="color:rgb(27, 28, 29);">Users (Student) - StudentLearningProgress</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个学生在不同课程/单元有学习进度)。</font>
14. **<font style="color:rgb(27, 28, 29);">Courses/TeachingPlans - StudentLearningProgress</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (课程/单元被多个学生学习)。</font>
15. **<font style="color:rgb(27, 28, 29);">Users (Student) - StudentQueries</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个学生可以发起多次问答交互)。</font>
16. **<font style="color:rgb(27, 28, 29);">Users (Admin) - KnowledgeBaseArticles</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (一个管理员可以上传多篇知识库文章)。</font>
17. **<font style="color:rgb(27, 28, 29);">KnowledgeBaseArticles - Questions</font>**<font style="color:rgb(27, 28, 29);">: 一对多 (可选，一个知识点可以出多个题目)。</font>
18. **<font style="color:rgb(27, 28, 29);">LLMModels</font>**<font style="color:rgb(27, 28, 29);">: 独立配置表，被 </font>`<font style="color:rgb(87, 91, 95);">AgentConfigurations</font>`<font style="color:rgb(27, 28, 29);"> 或系统其他部分引用。</font>
19. **<font style="color:rgb(27, 28, 29);">SystemLogs</font>**<font style="color:rgb(27, 28, 29);">: 记录系统各类活动，可关联 </font>`<font style="color:rgb(87, 91, 95);">Users</font>`<font style="color:rgb(27, 28, 29);">。</font>
20. **<font style="color:rgb(27, 28, 29);">AgentConfigurations (高级)</font>**<font style="color:rgb(27, 28, 29);">: 引用 </font>`<font style="color:rgb(87, 91, 95);">LLMModels</font>`<font style="color:rgb(27, 28, 29);">。</font>
21. **<font style="color:rgb(27, 28, 29);">Skills - SkillDependencies (高级)</font>**<font style="color:rgb(27, 28, 29);">: 多对多，通过关联表实现技能图谱。</font>
22. **<font style="color:rgb(27, 28, 29);">StudentLearningProgress - Skills (高级)</font>**<font style="color:rgb(27, 28, 29);">: 可关联学生掌握的技能。</font>

---

**<font style="color:rgb(27, 28, 29);">关键考量点：</font>**

+ **<font style="color:rgb(27, 28, 29);">JSON字段的使用：</font>**<font style="color:rgb(27, 28, 29);"> 对于灵活性要求高或结构不固定的数据（如</font>`<font style="color:rgb(87, 91, 95);">ChunkedContent</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">Keywords</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">RelevantSources</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">Configuration</font>`<font style="color:rgb(27, 28, 29);">），使用JSON类型可以简化初期设计，但查询性能和数据一致性需要额外关注。</font>
+ **<font style="color:rgb(27, 28, 29);">知识库与RAG：</font>**`<font style="color:rgb(87, 91, 95);">KnowledgeBaseArticles</font>`<font style="color:rgb(27, 28, 29);"> 表是RAG的核心。</font>`<font style="color:rgb(87, 91, 95);">ChunkedContent</font>`<font style="color:rgb(27, 28, 29);"> 和 </font>`<font style="color:rgb(87, 91, 95);">VectorEmbedding</font>`<font style="color:rgb(27, 28, 29);"> 的存储方式（直接存或存路径/ID指向外部向量数据库）需要根据实际情况选择。</font>
+ **<font style="color:rgb(27, 28, 29);">学情分析数据：</font>**`<font style="color:rgb(87, 91, 95);">StudentSubmissions</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">StudentAnswers</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">StudentLearningProgress</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">StudentQueries</font>`<font style="color:rgb(27, 28, 29);"> 是学情分析的主要数据源。需要设计高效的查询和聚合来支持仪表盘。</font>
+ **<font style="color:rgb(27, 28, 29);">多智能体数据：</font>**`<font style="color:rgb(87, 91, 95);">AgentConfigurations</font>`<font style="color:rgb(27, 28, 29);"> 是MAS架构的基础，记录各智能体的配置和状态。智能体间的交互可以记录在</font>`<font style="color:rgb(87, 91, 95);">SystemLogs</font>`<font style="color:rgb(27, 28, 29);">或专门的交互日志表中。</font>
+ **<font style="color:rgb(27, 28, 29);">索引：</font>**<font style="color:rgb(27, 28, 29);"> 对经常用于查询条件的字段（如FK, </font>`<font style="color:rgb(87, 91, 95);">Username</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">Email</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">Timestamp</font>`<font style="color:rgb(27, 28, 29);">等）创建索引至关重要。</font>
+ **<font style="color:rgb(27, 28, 29);">数据量与性能：</font>**<font style="color:rgb(27, 28, 29);"> 对于预期数据量大的表（如</font>`<font style="color:rgb(87, 91, 95);">StudentAnswers</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">SystemLogs</font>`<font style="color:rgb(27, 28, 29);">, </font>`<font style="color:rgb(87, 91, 95);">StudentQueries</font>`<font style="color:rgb(27, 28, 29);">），需要考虑分区、归档等策略。</font>
+ **<font style="color:rgb(27, 28, 29);">安全性：</font>**<font style="color:rgb(27, 28, 29);"> 密码哈希、API密钥加密、角色权限控制。</font>
+ **<font style="color:rgb(27, 28, 29);">本地知识库100MB限制：</font>**`<font style="color:rgb(87, 91, 95);">KnowledgeBaseArticles</font>`<font style="color:rgb(27, 28, 29);"> 表中存储原始内容和处理后的文本/向量。需要高效的文本提取、分块和索引策略。如果100MB指原始文件大小，那么处理后的数据可能会更大，需要规划好存储。</font>

---

<font style="color:rgb(27, 28, 29);">这份数据字典和数据库设计应该能覆盖您SRS中描述的大部分核心需求，并为高级功能的实现奠定基础。在实际开发过程中，还需要根据具体的技术选型和进一步的细化设计进行调整。</font>

<font style="color:rgb(27, 28, 29);"></font>

[从零开始部署Dify：后端与前端服务完整指南_dify部署服务要求-CSDN博客](https://blog.csdn.net/engchina/article/details/145434285)

[用Dify零代码给你的网站做AI客服！ - 公众号-JavaEdge - 博客园](https://www.cnblogs.com/JavaEdge/p/18714133)

