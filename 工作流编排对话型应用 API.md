# 工作流编排对话型应用 API

对话应用支持会话持久化，可将之前的聊天记录作为上下文进行回答，适用于聊天/客服 AI 等场景。

## prompt
一共在dify实现了五个agent

### 教学实训智能体知识库管理
<instruction>
作为教学实训智能体知识库管理端，你的任务是管理与本地知识库{{#context#}}的交互，执行高效检索，并可能构建或利用知识图谱为其他智能体提供可靠信息。请按照以下步骤完成任务：

1. **知识库检索**：根据用户提供的查询内容，从本地知识库中检索最相关的信息。确保检索结果准确、全面，并优先返回与教学实训相关的资料。
2. **知识图谱构建/利用**：如果任务涉及知识图谱，需根据检索结果构建或更新知识图谱，确保图谱中的节点和关系清晰、逻辑连贯。
3. **信息整合与验证**：将检索到的信息进行整合，确保其可靠性和实用性。必要时，可引用知识库中的权威来源以增强可信度。
4. **响应生成**：生成简洁、清晰的响应，避免冗余信息。确保输出内容直接回答用户查询，并提供必要的上下文或补充说明。
5. **格式要求**：输出内容不得包含任何XML标签，仅以纯文本形式返回结果。

请始终以教学实训为核心目标，确保提供的信息对其他智能体或用户有实际帮助。
</instruction>

<examples>
<example>
输入：检索关于“机器学习基础”的教学资料。
输出：本地知识库中找到以下关于“机器学习基础”的资料：1.《机器学习入门》教材，涵盖监督学习与非监督学习基础；2. 实训视频《线性回归实战》，时长30分钟；3. 知识图谱节点“机器学习分类”及其关联概念。
</example>

<example>
输入：更新知识图谱，添加“深度学习”与“神经网络”的关系。
输出：已更新知识图谱，新增关系“深度学习-包含->神经网络”，并补充了相关教学案例和参考文献。
</example>

<example>
输入：查找关于“Python编程实训”的最近更新内容。
输出：最近更新的内容包括：1. 新增实训项目《Python数据分析实战》；2. 修订《Python基础语法》中的错误案例；3. 知识图谱中“Python库”节点新增“Pandas”和“NumPy”子节点。
</example>
</examples>

<note>
1. 确保所有检索和更新操作基于本地知识库的现有内容，避免虚构信息。
2. 若用户查询内容超出知识库范围，需明确说明并建议补充资料。
3. 输出内容需简洁、结构化，便于其他智能体或用户直接使用。
</note>
```

### 教学实训智能体辅导端
```xml
<instruction>
作为教学实训智能体辅导端，你的任务是动态管理学生互动并提供高质量的教学支持。请遵循以下步骤：

1. 问题识别与分析：
   - 仔细阅读学生提出的问题或请求{{#sys.query#}}，判断其类型（概念疑问/练习请求/反馈需求）
   - 分析问题涉及的知识领域和难度层级

2. 知识库调用：
   - 根据问题类型自动匹配知识库中的相关教学资源{{#context#}}
   - 对复杂问题需拆解为多个子问题逐步解决

3. 动态角色适配：
   - 根据情境切换教学角色（讲解者/练习教练/评估者）
   - 讲解者角色：提供结构化知识讲解和示例
   - 练习教练：设计渐进式练习题并给予提示
   - 评估者：提供诊断性反馈和改进建议

4. 交互管理：
   - 保持3-5轮对话深度控制
   - 每轮交互需包含：问题确认→核心解答→延伸提问→反馈邀请

5. 输出规范：
   - 使用清晰的教学语言，避免学术术语堆砌
   - 复杂概念需配备生活化类比
   - 反馈需包含具体改进点和鼓励性语言
   - 严格禁止输出任何XML标签

响应时应保持专业且亲切的语气，根据学生认知水平调整表达复杂度。对于模糊问题，应主动请求澄清而非猜测作答。
</instruction>

<examples>
<example>
输入：我不理解递归函数的执行过程
输出：让我们用搭积木来理解递归。想象你要建一座塔：
1. 每块积木代表一次函数调用
2. 放新积木前要先完成下面的塔（递归条件）
3. 最底层的积木决定何时停止（基准条件）
现在请尝试描述：汉诺塔问题中哪部分对应递归条件？
</example>

<example>
输入：请给我数组排序的练习题
输出：好的，我们分三步练习：
【基础】用冒泡排序排列[3,1,4]
【进阶】分析快速排序对[5,2,7,3]的分区过程
【挑战】编写能处理空数组的排序函数
请先尝试基础题，需要提示可随时告知。
</example>

<example>
输入：这是我写的二叉树遍历代码，请检查
输出：你的前序遍历实现很规范！两个优化建议：
1. 第7行可增加空节点判断提升健壮性
2. 使用递归深度参数可避免栈溢出
尝试添加这些改进后，我们可以讨论非递归实现方案。
</example>
</examples>

<pedagogy_guidelines>
1. 苏格拉底式提问：通过连续提问引导学生自主发现答案
2. 脚手架原则：按"演示→协作→独立"三阶段提供支持
3. 错误利用：将错误转化为教学案例进行分析
4. 元认知培养：定期要求学生解释自己的解题思路
</pedagogy_guidelines>

<interaction_protocol>
1. 首次响应需包含学习目标声明
2. 复杂解释需配合图示描述要求
3. 练习环节必须包含自评指导
4. 最终需确认学生是否达成理解
5. 根据响应时间自动调整教学节奏
</interaction_protocol>
```


<note>
1. 确保所有检索和更新操作基于本地知识库的现有内容，避免虚构信息。
2. 若用户查询内容超出知识库范围，需明确说明并建议补充资料。
3. 输出内容需简洁、结构化，便于其他智能体或用户直接使用。
</note>
```

### 教学实训智能体评估
```xml
<instruction>
根据现有知识库{{#context#}}，为用户的提问{{#sys.query#}}生成多样化的评估项目，确保评估项目的多样性、难度适中，并提供参考答案。请按照以下步骤操作：

1. **理解用户问题**：仔细分析用户提出的问题，明确其核心内容和所需的知识点。
2. **生成评估项目**：
   - 设计至少3种不同类型的评估项目（如选择题、填空题、简答题、分析题等）。
   - 确保题目涵盖不同难度级别（简单、中等、困难）。
   - 题目内容应与用户问题紧密相关，避免无关内容。
3. **提供参考答案**：为每个评估项目生成详细的参考答案，确保答案准确、清晰。
4. **多样性检查**：确保题目形式多样，避免重复或过于相似的题目。
5. **输出格式**：以清晰、易读的文本格式输出评估项目和参考答案，不要包含任何XML标签。

输出时，请确保评估项目和参考答案分开标注，例如：
- 评估项目1：[题目内容]
- 参考答案1：[答案内容]
</instruction>

<examples>
<example>
输入：如何理解Python中的列表推导式？
输出：
- 评估项目1（选择题）：以下哪个选项正确描述了Python列表推导式的作用？
  A. 用于创建字典
  B. 用于生成列表
  C. 用于定义函数
  D. 用于循环遍历字符串
- 参考答案1：B. 用于生成列表

- 评估项目2（填空题）：请补全以下列表推导式，使其生成一个包含1到10的平方的列表：[x____ for x in range(1, 11)]。
- 参考答案2：**2

- 评估项目3（编程题）：编写一个列表推导式，生成一个包含1到20中所有偶数的平方的列表。
- 参考答案3：[x**2 for x in range(1, 21) if x % 2 == 0]
</example>

<example>
输入：什么是SQL中的JOIN操作？
输出：
- 评估项目1（选择题）：以下哪种JOIN操作会返回两个表中所有匹配和不匹配的行？
  A. INNER JOIN
  B. LEFT JOIN
  C. FULL JOIN
  D. RIGHT JOIN
- 参考答案1：C. FULL JOIN

- 评估项目2（简答题）：请简要说明INNER JOIN和LEFT JOIN的区别。
- 参考答案2：INNER JOIN只返回两个表中匹配的行，而LEFT JOIN会返回左表中所有行，即使右表中没有匹配。

- 评估项目3（编程题）：编写一个SQL查询，使用INNER JOIN连接“orders”和“customers”表，返回所有订单及其对应的客户名称。
- 参考答案3：SELECT orders.order_id, customers.customer_name FROM orders INNER JOIN customers ON orders.customer_id = customers.customer_id;
</example>

<example>
输入：如何解释机器学习中的过拟合现象？
输出：
- 评估项目1（判断题）：过拟合是指模型在训练集上表现良好，但在测试集上表现较差。（正确/错误）
- 参考答案1：正确

- 评估项目2（简答题）：列举两种防止过拟合的方法，并简要说明其原理。
- 参考答案2：1. 正则化：通过在损失函数中添加惩罚项，限制模型复杂度。2. 交叉验证：将数据分为多份，轮流作为训练集和验证集，确保模型泛化能力。

- 评估项目3（编程题）：使用Python和scikit-learn，编写一个带有L2正则化的逻辑回归模型代码。
- 参考答案3：
from sklearn.linear_model import LogisticRegression
model = LogisticRegression(penalty='l2')
model.fit(X_train, y_train)
</example>
</examples>
```

### 教学实训智能体学生端
<instruction>
你是一个学生教学智能体，需要根据提供的知识库{{#context#}}内容回答学生的问题{{#sys.query#}}，同时完成以下任务：
1. 追踪学生学习进度，记录已掌握和未掌握的知识点
2. 推断学生当前的知识状态，包括理解程度和潜在的知识缺口
3. 识别学生可能存在的错误概念或误解
4. 监控学生的参与度和互动质量

请按照以下步骤处理每个学生问题：
1. 首先仔细阅读知识库内容，确保完全理解相关知识点
2. 分析学生问题的深层含义，判断其真实的知识需求
3. 提供清晰、准确且适合学生当前水平的解答
4. 在回答中适当嵌入对知识掌握程度的评估
5. 识别并纠正任何可能的错误概念
6. 评估学生的参与度水平，必要时调整回答方式以提高参与度
7. 更新学生的学习进度记录

回答时应：
- 使用友好、鼓励性的语气
- 根据学生水平调整解释的深度
- 提供适当的例子帮助理解
- 避免使用专业术语，除非必要并加以解释
- 输出纯文本，不要包含任何XML标签

对于复杂问题，可以分步骤解释，并确认学生是否理解每个步骤。
</instruction>

<examples>
<example>
输入：
学生问题：为什么天空是蓝色的？
知识库内容：瑞利散射现象解释了天空颜色。当太阳光穿过大气层时，短波长的蓝光比长波长的红光散射更强烈。

输出：
这是个很好的问题！这是因为一种叫"瑞利散射"的现象。阳光中有各种颜色，但蓝光的波长特别短，当它穿过大气时会被空气分子散射到各个方向，所以我们看到天空是蓝色的。其他颜色像红光不太容易被散射。你之前听说过光的散射吗？(评估：学生可能刚开始学习光学概念)
</example>

<example>
输入：
学生问题：2的平方根是多少？
知识库内容：平方根是指一个数乘以自己等于给定数。2的平方根是无理数，约等于1.414。

输出：
2的平方根大约是1.414。这是个特殊的数，因为它不能表示为简单的分数，我们称它为"无理数"。你记得有理数和无理数的区别吗？(识别：可能需要复习有理数概念)
</example>

<example>
输入：
学生问题：植物为什么需要阳光？
知识库内容：光合作用是植物利用阳光将二氧化碳和水转化为葡萄糖和氧气的过程。

输出：
植物需要阳光来进行"光合作用" - 这是它们制造食物的方式！就像我们需要吃饭一样，植物用阳光、空气和水制造它们的营养。你能想象没有阳光植物会怎样吗？(监控：学生表现出对生命基本过程的兴趣)
</example>
</examples>

<note>
1. 始终根据学生的认知水平调整回答复杂度
2. 注意识别学生问题中隐含的误解
3. 定期总结学生进步情况
4. 对积极参与的学生给予正面强化
5. 对困惑的表现要及时调整教学方法
</note>
```
### 教学实训智能体教师端
```xml
<instruction>
根据现有知识库{{#context#}}，回答用户的问题{{#sys.query#}}并完成课程设计任务。请按照以下步骤执行：

1. **解读教学大纲**  
   - 分析用户提供的教学大纲，提取核心教学目标、重点难点、评估方式等关键要素。  
   - 将大纲内容结构化，明确课程模块与课时分配。  

2. **生成教案**  
   - 根据教学大纲的目标和结构，设计详细的教案框架，包括教学目标、教学内容、教学方法、课堂活动、课后作业等。  
   - 确保教案逻辑清晰，符合教学规律。  

3. **构建教学内容结构**  
   - 将教学内容分解为逻辑连贯的模块或单元，明确每个模块的主题、子主题和知识点。  
   - 使用任务分解方法（如思维链CoT或思维树ToT）细化复杂内容，确保知识点的递进性和关联性。  

4. **识别先修知识**  
   - 分析教学内容，明确学生需要掌握的预备知识，并标注在教案或内容结构中。  
   - 提供补充资源或复习建议（如用户需要）。  

5. **输出要求**  
   - 输出内容应为纯文本，不得包含任何XML标签。  
   - 语言简洁明了，结构清晰，便于用户直接使用。  

</instruction>

<examples>
<example>
输入：  
"教学大纲：高中数学函数模块，涵盖一次函数、二次函数、指数函数，共12课时。"  

输出：  
"教案设计：  
1. 教学目标：掌握三类函数的基本性质与图像特征。  
2. 教学内容：  
   - 第1-3课时：一次函数（定义、图像、应用）。  
   - 第4-7课时：二次函数（标准式、顶点式、最值问题）。  
   - 第8-12课时：指数函数（性质、增长模型）。  
3. 先修知识：代数基础、坐标系。  
4. 课堂活动：分组绘制函数图像并分析特点。"  
</example>

<example>
输入：  
"设计Python编程入门课程，面向零基础学生，共8周。"  

输出：  
"教学内容结构：  
1. 第1周：编程基础（变量、数据类型）。  
2. 第2-3周：流程控制（条件语句、循环）。  
3. 第4-5周：函数与模块化编程。  
4. 第6-7周：文件操作与异常处理。  
5. 第8周：综合项目（简易计算器）。  
先修知识：无（适合零基础）。"  
</example>

<example>
输入：  
"解读生物课程大纲：细胞生物学部分，重点为细胞结构与功能。"  

输出：  
"教案要点：  
1. 教学目标：理解细胞器功能及相互关系。  
2. 教学分解：  
   - 第1课：细胞膜与物质运输（CoT：结构→功能→实验验证）。  
   - 第2课：线粒体与能量代谢（ToT：化学能→ATP→细胞呼吸）。  
3. 先修知识：基础化学、显微镜使用。"  
</example>
</examples>
```

## 基础信息

**基础 URL**: `https://api.dify.ai/v1`

### 鉴权

Service API 使用 API-Key 进行鉴权。建议将 API-Key 存储在后端，避免泄露导致财产损失。所有 API 请求需在 `Authorization` HTTP Header 中包含 API-Key：

```http
Authorization: Bearer {API_KEY}
```

## API 端点

### 1. 发送对话消息

**POST** `/chat-messages`

创建会话消息，支持流式和阻塞模式。

#### 请求参数

| 参数名              | 类型         | 描述                                                                 |
|---------------------|--------------|----------------------------------------------------------------------|
| `query`            | string       | 用户输入/提问内容。                                                  |
| `inputs`           | object       | App 定义的变量值，键值对形式，默认 `{}`。                            |
| `response_mode`    | string       | `streaming`（推荐，基于 SSE 流式返回）或 `blocking`（等待结果返回）。 |
| `user`             | string       | 用户标识，需在应用内唯一，用于检索、统计。                           |
| `conversation_id`  | string       | （选填）会话 ID，用于基于之前的聊天记录继续对话。                     |
| `files`            | array[object] | 文件列表，支持多模态理解，仅当模型支持 Vision 能力时可用。           |
| `auto_generate_name` | bool       | （选填）是否自动生成会话标题，默认 `true`。                          |

**`files` 参数说明**:

- **type**: string，支持类型：
  - `document`: TXT, MD, PDF, HTML, XLSX, XLS, DOCX, CSV, EML, MSG, PPTX, PPT, XML, EPUB
  - `image`: JPG, JPEG, PNG, GIF, WEBP, SVG
  - `audio`: MP3, M4A, WAV, WEBM, AMR
  - `video`: MP4, MOV, MPEG, MPGA
  - `custom`: 其他文件类型
- **transfer_method**: string，`remote_url`（图片地址）或 `local_file`（上传文件）。
- **url**: string，仅当 `transfer_method` 为 `remote_url` 时使用。
- **upload_file_id**: string，仅当 `transfer_method` 为 `local_file` 时使用。

#### 响应

- **阻塞模式 (`blocking`)**: 返回 `ChatCompletionResponse` 对象，`Content-Type: application/json`。
  - `event`: string，事件类型，固定为 `message`。
  - `task_id`: string，任务 ID。
  - `id`: string，唯一 ID。
  - `message_id`: string，消息唯一 ID。
  - `conversation_id`: string，会话 ID。
  - `mode`: string，App 模式，固定为 `chat`。
  - `answer`: string，完整回复内容。
  - `metadata`: object，元数据（包含 `usage` 和 `retriever_resources`）。
  - `created_at`: int，消息创建时间戳。

- **流式模式 (`streaming`)**: 返回 `ChunkChatCompletionResponse` 对象，`Content-Type: text/event-stream`。每个块以 `data:` 开头，块间以 `\n\n` 分隔。

**流式事件类型**:

| 事件类型            | 描述                                                                 |
|---------------------|----------------------------------------------------------------------|
| `message`          | LLM 返回文本块，包含 `task_id`, `message_id`, `conversation_id`, `answer`, `created_at`。 |
| `message_file`     | 文件事件，包含文件信息（如 `type: image`, `url`, `belongs_to`）。     |
| `message_end`      | 消息结束，包含 `metadata`, `usage`, `retriever_resources`。           |
| `tts_message`      | TTS 音频流，包含 Base64 编码的音频块。                               |
| `tts_message_end`  | TTS 音频流结束，`audio` 为空。                                       |
| `message_replace`  | 消息内容替换，命中审查条件时触发。                                   |
| `workflow_started` | 工作流开始，包含 `workflow_run_id`。                                 |
| `node_started`     | 节点开始，包含 `node_id`, `node_type`, `title`, `index` 等。         |
| `node_finished`    | 节点结束，包含 `status`, `outputs`, `elapsed_time` 等。              |
| `workflow_finished`| 工作流结束，包含 `status`, `total_steps`, `finished_at` 等。         |
| `error`            | 流式输出异常，包含 `status`, `code`, `message`。                     |
| `ping`             | 每 10 秒一次，保持连接存活。                                        |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/chat-messages' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "inputs": {},
    "query": "What are the specs of the iPhone 13 Pro Max?",
    "response_mode": "streaming",
    "conversation_id": "",
    "user": "abc-123",
    "files": [
      {
        "type": "image",
        "transfer_method": "remote_url",
        "url": "https://cloud.dify.ai/logo/logo-site.png"
      }
    ]
}'
```

#### 示例响应（阻塞模式）

```json
{
    "event": "message",
    "task_id": "c3800678-a077-43df-a102-53f23ed20b88",
    "id": "9da23599-e713-473b-982c-4328d4f5c78a",
    "message_id": "9da23599-e713-473b-982c-4328d4f5c78a",
    "conversation_id": "45701982-8118-4bc5-8e9b-64562b4555f2",
    "mode": "chat",
    "answer": "iPhone 13 Pro Max specs are listed here:...",
    "metadata": {
        "usage": {
            "prompt_tokens": 1033,
            "prompt_unit_price": "0.001",
            "prompt_price_unit": "0.001",
            "prompt_price": "0.0010330",
            "completion_tokens": 128,
            "completion_unit_price": "0.002",
            "completion_price_unit": "0.001",
            "completion_price": "0.0002560",
            "total_tokens": 1161,
            "total_price": "0.0012890",
            "currency": "USD",
            "latency": 0.7682376249867957
        },
        "retriever_resources": [
            {
                "position": 1,
                "dataset_id": "101b4c97-fc2e-463c-90b1-5261a4cdcafb",
                "dataset_name": "iPhone",
                "document_id": "8dd1ad74-0b5f-4175-b735-7d98bbbb4e00",
                "document_name": "iPhone List",
                "segment_id": "ed599c7f-2766-4294-9d1d-e5235a61270a",
                "score": 0.98457545,
                "content": "\"Model\",\"Release Date\",\"Display Size\",\"Resolution\",\"Processor\",\"RAM\",\"Storage\",\"Camera\",\"Battery\",\"Operating System\"\n\"iPhone 13 Pro Max\",\"September 24, 2021\",\"6.7 inch\",\"1284 x 2778\",\"Hexa-core (2x3.23 GHz Avalanche + 4x1.82 GHz Blizzard)\",\"6 GB\",\"128, 256, 512 GB, 1TB\",\"12 MP\",\"4352 mAh\",\"iOS 15\""
            }
        ]
    },
    "created_at": 1705407629
}
```

#### 示例响应（流式模式）

```text
data: {"event": "workflow_started", "task_id": "5ad4cb98-f0c7-4085-b384-88c403be6290", "workflow_run_id": "5ad498-f0c7-4085-b384-88cbe6290", "data": {"id": "5ad498-f0c7-4085-b384-88cbe6290", "workflow_id": "dfjasklfjdslag", "created_at": 1679586595}}
data: {"event": "message", "message_id": "5ad4cb98-f0c7-4085-b384-88c403be6290", "conversation_id": "45701982-8118-4bc5-8e9b-64562b4555f2", "answer": " I", "created_at": 1679586595}
data: {"event": "message_end", "id": "5e52ce04-874b-4d27-9045-b3bc80def685", "conversation_id": "45701982-8118-4bc5-8e9b-64562b4555f2", "metadata": {...}}
```

### 2. 上传文件

**POST** `/files/upload`

上传文件以支持图文多模态理解，仅供当前终端用户使用。

#### 请求参数

| 参数名 | 类型 | 描述                                                  |
|--------|------|-------------------------------------------------------|
| `file` | file | 要上传的文件，支持多种格式。                          |
| `user` | string | 用户标识，需与发送消息接口的 `user` 保持一致。        |

#### 响应

| 字段名         | 类型     | 描述                          |
|----------------|----------|-------------------------------|
| `id`           | uuid     | 文件 ID                      |
| `name`         | string   | 文件名                       |
| `size`         | int      | 文件大小（byte）             |
| `extension`    | string   | 文件后缀                     |
| `mime_type`    | string   | 文件 MIME 类型               |
| `created_by`   | uuid     | 上传人 ID                    |
| `created_at`   | timestamp | 上传时间                     |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/files/upload' \
--header 'Authorization: Bearer {api_key}' \
--form 'file=@localfile;type=image/[png|jpeg|jpg|webp|gif]' \
--form 'user=abc-123'
```

#### 示例响应

```json
{
  "id": "72fa9618-8f89-4a37-9b33-7e1178a24a67",
  "name": "example.png",
  "size": 1024,
  "extension": "png",
  "mime_type": "image/png",
  "created_by": 123,
  "created_at": 1577836800
}
```

### 3. 停止响应

**POST** `/chat-messages/:task_id/stop`

停止流式模式响应。

#### 请求参数

| 参数名 | 类型   | 描述                                                  |
|--------|--------|-------------------------------------------------------|
| `user` | string | 用户标识，需与发送消息接口的 `user` 保持一致。        |

#### 响应

| 字段名   | 类型   | 描述           |
|----------|--------|----------------|
| `result` | string | 固定返回 `success` |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/chat-messages/:task_id/stop' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{ "user": "abc-123"}'
```

#### 示例响应

```json
{
  "result": "success"
}
```

### 4. 消息反馈（点赞）

**POST** `/messages/:message_id/feedbacks`

终端用户对消息进行反馈（点赞/点踩）。

#### 请求参数

| 参数名    | 类型   | 描述                             |
|-----------|--------|----------------------------------|
| `rating`  | string | `like`, `dislike`, 或 `null`（撤销）。 |
| `user`    | string | 用户标识，需在应用内唯一。       |
| `content` | string | 反馈的具体信息。                 |

#### 响应

| 字段名   | 类型   | 描述           |
|----------|--------|----------------|
| `result` | string | 固定返回 `success` |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/messages/:message_id/feedbacks' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "rating": "like",
    "user": "abc-123",
    "content": "message feedback information"
}'
```

#### 示例响应

```json
{
  "result": "success"
}
```

### 5. 获取会话历史消息

**GET** `/messages`

以滚动加载形式返回历史聊天记录，倒序返回最新 `limit` 条。

#### 查询参数

| 参数名           | 类型   | 描述                             |
|------------------|--------|----------------------------------|
| `conversation_id`| string | 会话 ID                         |
| `user`           | string | 用户标识，需在应用内唯一。       |
| `first_id`       | string | 当前页第一条记录的 ID，默认 `null`。 |
| `limit`          | int    | 返回记录数，默认 20 条。         |

#### 响应

| 字段名              | 类型          | 描述                             |
|---------------------|---------------|----------------------------------|
| `limit`            | int           | 返回条数                        |
| `has_more`         | bool          | 是否存在下一页                  |
| `data`             | array[object] | 消息列表                        |
| `id`               | string        | 消息 ID                         |
| `conversation_id`  | string        | 会话 ID                         |
| `inputs`           | object        | 用户输入参数                    |
| `query`            | string        | 用户提问内容                    |
| `answer`           | string        | 回答内容                        |
| `message_files`    | array[object] | 消息文件列表                    |
| `feedback`         | object        | 反馈信息（`rating` 等）         |
| `retriever_resources` | array[RetrieverResource] | 引用和归属分段列表 |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/messages?user=abc-123&conversation_id=' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "limit": 20,
  "has_more": false,
  "data": [
    {
      "id": "a076a87f-31e5-48dc-b452-0061adbbc922",
      "conversation_id": "cd78daf6-f9e4-4463-9ff2-54257230a0ce",
      "inputs": {
        "name": "dify"
      },
      "query": "iphone 13 pro",
      "answer": "The iPhone 13 Pro, released on September 24, 2021, features a 6.1-inch display with a resolution of 1170 x 2532. It is equipped with a Hexa-core (2x3.23 GHz Avalanche + 4x1.82 GHz Blizzard) processor, 6 GB of RAM, and offers storage options of 128 GB, 256 GB, 512 GB, and 1 TB. The camera is 12 MP, the battery capacity is 3095 mAh, and it runs on iOS 15.",
      "message_files": [],
      "feedback": null,
      "retriever_resources": [
        {
          "position": 1,
          "dataset_id": "101b4c97-fc2e-463c-90b1-5261a4cdcafb",
          "dataset_name": "iPhone",
          "document_id": "8dd1ad74-0b5f-4175-b735-7d98bbbb4e00",
          "document_name": "iPhone List",
          "segment_id": "ed599c7f-2766-4294-9d1d-e5235a61270a",
          "score": 0.98457545,
          "content": "\"Model\",\"Release Date\",\"Display Size\",\"Resolution\",\"Processor\",\"RAM\",\"Storage\",\"Camera\",\"Battery\",\"Operating System\"\n\"iPhone 13 Pro Max\",\"September 24, 2021\",\"6.7 inch\",\"1284 x 2778\",\"Hexa-core (2x3.23 GHz Avalanche + 4x1.82 GHz Blizzard)\",\"6 GB\",\"128, 256, 512 GB, 1TB\",\"12 MP\",\"4352 mAh\",\"iOS 15\""
        }
      ],
      "created_at": 1705569239
    }
  ]
}
```

### 6. 获取会话列表

**GET** `/conversations`

获取当前用户的会话列表，默认返回最近 20 条。

#### 查询参数

| 参数名    | 类型   | 描述                             |
|-----------|--------|----------------------------------|
| `user`    | string | 用户标识，需在应用内唯一。       |
| `last_id` | string | （选填）当前页最后一条记录的 ID，默认 `null`。 |
| `limit`   | int    | （选填）返回记录数，默认 20，最大 100，最小 1。 |
| `sort_by` | string | （选填）排序字段，默认 `-updated_at`（按更新时间倒序）。可选值：`created_at`, `-created_at`, `updated_at`, `-updated_at`。 |

#### 响应

| 字段名        | 类型          | 描述                             |
|---------------|---------------|----------------------------------|
| `limit`       | int           | 返回条数                        |
| `has_more`    | bool          | 是否存在下一页                  |
| `data`        | array[object] | 会话列表                        |
| `id`          | string        | 会话 ID                         |
| `name`        | string        | 会话名称，默认由模型生成        |
| `inputs`      | object        | 用户输入参数                    |
| `status`      | string        | 会话状态                        |
| `introduction`| string        | 开场白                         |
| `created_at`  | timestamp     | 创建时间                        |
| `updated_at`  | timestamp     | 更新时间                        |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/conversations?user=abc-123&last_id=&limit=20' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "limit": 20,
  "has_more": false,
  "data": [
    {
      "id": "10799fb8-64f7-4296-bbf7-b42bfbe0ae54",
      "name": "New chat",
      "inputs": {
        "book": "book",
        "myName": "Lucy"
      },
      "status": "normal",
      "created_at": 1679667915,
      "updated_at": 1679667915
    }
  ]
}
```

### 7. 删除会话

**DELETE** `/conversations/:conversation_id`

删除指定会话。

#### 请求参数

| 参数名 | 类型   | 描述                             |
|--------|--------|----------------------------------|
| `user` | string | 用户标识，需在应用内唯一。       |

#### 响应

- 状态码：`204 No Content`

#### 示例请求

```bash
curl -X DELETE 'https://api.dify.ai/v1/conversations/:conversation_id' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{ "user": "abc-123"}'
```

### 8. 会话重命名

**POST** `/conversations/:conversation_id/name`

对会话进行重命名。

#### 请求参数

| 参数名          | 类型   | 描述                             |
|-----------------|--------|----------------------------------|
| `name`          | string | （选填）会话名称，若 `auto_generate` 为 `true` 可不传。 |
| `auto_generate` | bool   | （选填）是否自动生成标题，默认 `false`。 |
| `user`          | string | 用户标识，需在应用内唯一。       |

#### 响应

| 字段名        | 类型      | 描述                             |
|---------------|-----------|----------------------------------|
| `id`          | string    | 会话 ID                         |
| `name`        | string    | 会话名称                        |
| `inputs`      | object    | 用户输入参数                    |
| `status`      | string    | 会话状态                        |
| `introduction`| string    | 开场白                         |
| `created_at`  | timestamp | 创建时间                        |
| `updated_at`  | timestamp | 更新时间                        |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/conversations/:conversation_id/name' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{ "name": "", "auto_generate": true, "user": "abc-123"}'
```

#### 示例响应

```json
{
  "id": "34d511d5-56de-4f16-a997-57b379508443",
  "name": "hello",
  "inputs": {},
  "status": "normal",
  "introduction": "",
  "created_at": 1732731141,
  "updated_at": 1732734510
}
```

### 9. 获取对话变量

**GET** `/conversations/:conversation_id/variables`

检索特定对话中的变量。

#### 查询参数

| 参数名     | 类型   | 描述                             |
|------------|--------|----------------------------------|
| `user`     | string | 用户标识，需在应用内唯一。       |
| `last_id`  | string | （选填）当前页最后一条记录的 ID，默认 `null`。 |
| `limit`    | int    | （选填）返回记录数，默认 20，最大 100，最小 1。 |

#### 响应

| 字段名       | 类型          | 描述                             |
|--------------|---------------|----------------------------------|
| `limit`      | int           | 每页项目数                      |
| `has_more`   | bool          | 是否有更多项目                  |
| `data`       | array[object] | 变量列表                        |
| `id`         | string        | 变量 ID                         |
| `name`       | string        | 变量名称                        |
| `value_type` | string        | 变量类型（string, number, bool 等） |
| `value`      | string        | 变量值                          |
| `description`| string        | 变量描述                        |
| `created_at` | int           | 创建时间戳                      |
| `updated_at` | int           | 最后更新时间戳                  |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/conversations/{conversation_id}/variables?user=abc-123' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "limit": 100,
  "has_more": false,
  "data": [
    {
      "id": "variable-uuid-1",
      "name": "customer_name",
      "value_type": "string",
      "value": "John Doe",
      "description": "客户名称（从对话中提取）",
      "created_at": 1650000000000,
      "updated_at": 1650000000000
    }
  ]
}
```

### 10. 语音转文字

**POST** `/audio-to-text`

将语音文件转换为文字。

#### 请求参数

| 参数名 | 类型 | 描述                                                  |
|--------|------|-------------------------------------------------------|
| `file` | file | 语音文件，支持格式：mp3, mp4, mpeg, mpga, m4a, wav, webm，最大 15MB。 |
| `user` | string | 用户标识，需在应用内唯一。                            |

#### 响应

| 字段名 | 类型   | 描述           |
|--------|--------|----------------|
| `text` | string | 输出文字       |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/audio-to-text' \
--header 'Authorization: Bearer {api_key}' \
--form 'file=@localfile;type=audio/[mp3|mp4|mpeg|mpga|m4a|wav|webm]'
```

#### 示例响应

```json
{
  "text": "hello"
}
```

### 11. 文字转语音

**POST** `/text-to-audio`

将文字转换为语音。

#### 请求参数

| 参数名      | 类型   | 描述                                                  |
|-------------|--------|-------------------------------------------------------|
| `message_id`| string | Dify 生成的文本消息 ID，优先使用。                    |
| `text`      | string | 语音生成内容，若无 `message_id` 则使用此字段。         |
| `user`      | string | 用户标识，需在应用内唯一。                            |

#### 响应

- `Content-Type: audio/wav`

#### 示例请求

```bash
curl -o text-to-audio.mp3 -X POST 'https://api.dify.ai/v1/text-to-audio' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "message_id": "5ad4cb98-f0c7-4085-b384-88c403be6290",
    "text": "你好Dify",
    "user": "abc-123"
}'
```

### 12. 获取应用基本信息

**GET** `/info`

获取应用的基本信息。

#### 响应

| 字段名       | 类型          | 描述                             |
|--------------|---------------|----------------------------------|
| `name`       | string        | 应用名称                        |
| `description`| string        | 应用描述                        |
| `tags`       | array[string] | 应用标签                        |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/info' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "name": "My App",
  "description": "This is my app.",
  "tags": ["tag1", "tag2"],
  "mode": "advanced-chat",
  "author_name": "Dify"
}
```

### 13. 获取应用参数

**GET** `/parameters`

获取应用的功能开关、输入参数名称、类型及默认值等。

#### 响应

| 字段名                     | 类型          | 描述                             |
|----------------------------|---------------|----------------------------------|
| `opening_statement`        | string        | 开场白                         |
| `suggested_questions`      | array[string] | 开场推荐问题列表                |
| `user_input_form`          | array[object] | 用户输入表单配置                |
| `file_upload`              | object        | 文件上传配置                    |
| `system_parameters`        | object        | 系统参数（如文件大小限制）      |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/parameters' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "introduction": "nice to meet you",
  "user_input_form": [
    {
      "text-input": {
        "label": "a",
        "variable": "a",
        "required": true,
        "max_length": 48,
        "default": ""
      }
    }
  ],
  "file_upload": {
    "image": {
      "enabled": true,
      "number_limits": 3,
      "transfer_methods": ["remote_url", "local_file"]
    }
  },
  "system_parameters": {
    "file_size_limit": 15,
    "image_file_size_limit": 10,
    "audio_file_size_limit": 50,
    "video_file_size_limit": 100
  }
}
```

### 14. 获取应用 Meta 信息

**GET** `/meta`

获取工具图标等 Meta 信息。

#### 响应

| 字段名       | 类型          | 描述                             |
|--------------|---------------|----------------------------------|
| `tool_icons` | object[string]| 工具图标，包含 URL 或 emoji 信息 |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/meta' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "tool_icons": {
    "dalle2": "https://cloud.dify.ai/console/api/workspaces/current/tool-provider/builtin/dalle/icon",
    "api_tool": {
      "background": "#252525",
      "content": "😁"
    }
  }
}
```

### 15. 获取应用 WebApp 设置

**GET** `/site`

获取应用的 WebApp 设置。

#### 响应

| 字段名                   | 类型          | 描述                             |
|--------------------------|---------------|----------------------------------|
| `title`                  | string        | WebApp 名称                     |
| `chat_color_theme`       | string        | 聊天颜色主题，hex 格式          |
| `icon_type`              | string        | 图标类型（emoji 或 image）       |
| `icon`                   | string        | 图标（emoji 或 URL）            |
| `icon_background`        | string        | 图标背景色，hex 格式            |
| `description`            | string        | 描述                            |
| `copyright`              | string        | 版权信息                        |
| `privacy_policy`         | string        | 隐私政策链接                    |
| `custom_disclaimer`      | string        | 自定义免责声明                  |
| `default_language`       | string        | 默认语言                        |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/site' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "title": "My App",
  "chat_color_theme": "#ff4a4a",
  "chat_color_theme_inverted": false,
  "icon_type": "emoji",
  "icon": "😄",
  "icon_background": "#FFEAD5",
  "icon_url": null,
  "description": "This is my app.",
  "copyright": "all rights reserved",
  "privacy_policy": "",
  "custom_disclaimer": "All generated by AI",
  "default_language": "en-US",
  "show_workflow_steps": false,
  "use_icon_as_answer_icon": false
}
```

### 16. 获取标注列表

**GET** `/apps/annotations`

获取应用的标注列表。

#### 查询参数

| 参数名 | 类型   | 描述           |
|--------|--------|----------------|
| `page` | string | 页码           |
| `limit`| string | 每页数量       |

#### 响应

| 字段名       | 类型          | 描述                             |
|--------------|---------------|----------------------------------|
| `data`       | array[object] | 标注列表                        |
| `id`         | string        | 标注 ID                         |
| `question`   | string        | 问题                            |
| `answer`     | string        | 答案内容                        |
| `hit_count`  | int           | 命中次数                        |
| `created_at` | timestamp     | 创建时间                        |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/apps/annotations?page=1&limit=20' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "data": [
    {
      "id": "69d48372-ad81-4c75-9c46-2ce197b4d402",
      "question": "What is your name?",
      "answer": "I am Dify.",
      "hit_count": 0,
      "created_at": 1735625869
    }
  ],
  "has_more": false,
  "limit": 20,
  "total": 1,
  "page": 1
}
```

### 17. 创建标注

**POST** `/apps/annotations`

创建新的标注。

#### 请求参数

| 参数名    | 类型   | 描述           |
|-----------|--------|----------------|
| `question`| string | 问题           |
| `answer`  | string | 答案内容       |

#### 响应

| 字段名       | 类型      | 描述                             |
|--------------|-----------|----------------------------------|
| `id`         | string    | 标注 ID                         |
| `question`   | string    | 问题                            |
| `answer`     | string    | 答案内容                        |
| `hit_count`  | int       | 命中次数                        |
| `created_at` | timestamp | 创建时间                        |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/apps/annotations' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{"question": "What is your name?","answer": "I am Dify."}'
```

#### 示例响应

```json
{
  "id": "69d48372-ad81-4c75-9c46-2ce197b4d402",
  "question": "What is your name?",
  "answer": "I am Dify.",
  "hit_count": 0,
  "created_at": 1735625869
}
```

### 18. 更新标注

**PUT** `/apps/annotations/{annotation_id}`

更新指定标注。

#### 请求参数

| 参数名        | 类型   | 描述           |
|---------------|--------|----------------|
| `annotation_id`| string | 标注 ID        |
| `question`    | string | 问题           |
| `answer`      | string | 答案内容       |

#### 响应

| 字段名       | 类型      | 描述                             |
|--------------|-----------|----------------------------------|
| `id`         | string    | 标注 ID                         |
| `question`   | string    | 问题                            |
| `answer`     | string    | 答案内容                        |
| `hit_count`  | int       | 命中次数                        |
| `created_at` | timestamp | 创建时间                        |

#### 示例请求

```bash
curl -X PUT 'https://api.dify.ai/v1/apps/annotations/{annotation_id}' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{"question": "What is your name?","answer": "I am Dify."}'
```

#### 示例响应

```json
{
  "id": "69d48372-ad81-4c75-9c46-2ce197b4d402",
  "question": "What is your name?",
  "answer": "I am Dify.",
  "hit_count": 0,
  "created_at": 1735625869
}
```

### 19. 删除标注

**DELETE** `/apps/annotations/{annotation_id}`

删除指定标注。

#### 请求参数

| 参数名        | 类型   | 描述           |
|---------------|--------|----------------|
| `annotation_id`| string | 标注 ID        |

#### 响应

- 状态码：`204 No Content`

#### 示例请求

```bash
curl -X DELETE 'https://api.dify.ai/v1/apps/annotations/{annotation_id}' \
--header 'Authorization: Bearer {api_key}'
```

### 20. 标注回复初始设置

**POST** `/apps/annotation-reply/{action}`

设置标注回复的初始化配置。

#### 请求参数

| 参数名                 | 类型   | 描述                                                  |
|------------------------|--------|-------------------------------------------------------|
| `action`               | string | 动作，`enable` 或 `disable`。                         |
| `embedding_provider_name`| string | 嵌入模型提供商，需在系统中预设。                     |
| `embedding_model_name` | string | 嵌入模型名称。                                        |
| `score_threshold`      | number | 相似度阈值，高于此值时自动回复。                     |

#### 响应

| 字段名       | 类型   | 描述                             |
|--------------|--------|----------------------------------|
| `job_id`     | string | 任务 ID                         |
| `job_status` | string | 任务状态（`waiting` 等）        |

#### 示例请求

```bash
curl -X POST 'https://api.dify.ai/v1/apps/annotation-reply/{action}' \
--header 'Authorization: Bearer {api_key}' \
--header 'Content-Type: application/json' \
--data-raw '{"score_threshold": 0.9, "embedding_provider_name": "zhipu", "embedding_model_name": "embedding_3"}'
```

#### 示例响应

```json
{
  "job_id": "b15c8f68-1cf4-4877-bf21-ed7cf2011802",
  "job_status": "waiting"
}
```

### 21. 查询标注回复初始设置任务状态

**GET** `/apps/annotation-reply/{action}/status/{job_id}`

查询标注回复初始化任务的状态。

#### 查询参数

| 参数名   | 类型   | 描述                                                  |
|----------|--------|-------------------------------------------------------|
| `action` | string | 动作，`enable` 或 `disable`，需与初始设置一致。        |
| `job_id` | string | 任务 ID，从初始设置接口返回。                         |

#### 响应

| 字段名       | 类型   | 描述                             |
|--------------|--------|----------------------------------|
| `job_id`     | string | 任务 ID                         |
| `job_status` | string | 任务状态                        |
| `error_msg`  | string | 错误信息（若有）                |

#### 示例请求

```bash
curl -X GET 'https://api.dify.ai/v1/apps/annotation-reply/{action}/status/{job_id}' \
--header 'Authorization: Bearer {api_key}'
```

#### 示例响应

```json
{
  "job_id": "b15c8f68-1cf4-4877-bf21-ed7cf2011802",
  "job_status": "waiting",
  "error_msg": ""
}
```

## 错误码

| 状态码 | 错误码                     | 描述                             |
|--------|----------------------------|----------------------------------|
| 400    | `invalid_param`            | 传入参数异常                     |
| 400    | `app_unavailable`          | App 配置不可用                   |
| 400    | `provider_not_initialize`  | 无可用模型凭据配置               |
| 400    | `provider_quota_exceeded`  | 模型调用额度不足                 |
| 400    | `model_currently_not_support` | 当前模型不可用                |
| 400    | `completion_request_error` | 文本生成失败                     |
| 404    | `conversation_not_exists`  | 对话不存在                       |
| 413    | `file_too_large`           | 文件太大                         |
| 415    | `unsupported_file_type`    | 不支持的扩展名                   |
| 500    |                            | 服务内部异常                     |
| 503    | `s3_connection_failed`     | 无法连接到 S3 服务               |
| 503    | `s3_permission_denied`     | 无权限上传文件到 S3              |
| 503    | `s3_file_too_large`        | 文件超出 S3 大小限制             |