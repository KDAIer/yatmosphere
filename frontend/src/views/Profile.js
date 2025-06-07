// Profile.js
import { ref } from 'vue'
import axios from 'axios'

export const familyMembers = ref([])

const getCurrentAccount = () => {
  const account = localStorage.getItem('username')
  if (!account) console.warn('未在 localStorage 中找到 account，使用默认 admin')
  return account || 'admin'
}

export const loadFamilyMembersFromApi = async () => {
  const account = getCurrentAccount()
  try {
    const res = await axios.get('/api/family-members', { params: { account } })
    if (res.data.status === 200) {
      familyMembers.value = res.data.data.map((member, index) => ({
        ...member,
        id: index,
        isHome: member.isHome ?? false
      }))
      // 不要在这里写 localStorage，等合并完 todos 后再写
    } else {
      console.error('家庭成员请求失败:', res.data.msg)
    }
  } catch (err) {
    console.error('家庭成员请求异常:', err)
  }
}

/**
 * 把旧缓存里的 todos 合并到刚拉下来的 familyMembers.value 里
 */
export function mergeTodosFromStorage() {
  const stored = localStorage.getItem('familyMembers')
  if (!stored) return
  const oldMembers = JSON.parse(stored)
  familyMembers.value.forEach(member => {
    // 只用 name 来匹配，确保能找到旧数据
    const old = oldMembers.find(m => m.name === member.name)
    if (old && Array.isArray(old.todos)) {
      member.todos = old.todos
    }
  })
  // 合并完之后，一次性写回 localStorage
  localStorage.setItem('familyMembers', JSON.stringify(familyMembers.value))
}
