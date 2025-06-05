import { ref, watch } from 'vue'
import axios from 'axios'

export const familyMembers = ref([])

export function loadFamilyMembersFromStorage() {
  const stored = localStorage.getItem('familyMembers')
  if (stored) familyMembers.value = JSON.parse(stored)
}


const getCurrentAccount = () => {
  //const account = localStorage.getItem('account')
  const account = localStorage.getItem('username')
  if (!account) {
    console.warn('未在 localStorage 中找到 account，使用默认值 admin')
  }
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
        isHome: member.isHome ?? false // 如果后端没有 isHome 字段，设置默认值
      }))
      localStorage.setItem('familyMembers', JSON.stringify(familyMembers.value))
      console.log('家庭成员加载成功:', familyMembers.value)
    } else {
      console.error('家庭成员请求失败:', res.data.msg)
    }
  } catch (err) {
    console.error('家庭成员请求异常:', err)
  }
}